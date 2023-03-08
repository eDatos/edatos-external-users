package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentUtil;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorMessagesConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.core.service.DocumentService;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ChangePasswordDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountBaseDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalUserAccountMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/")
public class ExternalAccountResource extends AbstractResource {

    private static final String ENTITY_NAME = "userManagement";

    private final ExternalUserRepository externalUserRepository;
    private final ExternalUserService externalUserService;
    private final ExternalUserAccountMapper externalUserMapper;
    private final AuditEventPublisher auditPublisher;
    private final NotificationService notificationService;
    private final DocumentService documentService;

    public ExternalAccountResource(ExternalUserRepository externalUserRepository, ExternalUserService externalUserService, ExternalUserAccountMapper externalUserMapper,
            AuditEventPublisher auditPublisher, NotificationService notificationService, DocumentService documentService) {
        this.externalUserRepository = externalUserRepository;
        this.externalUserService = externalUserService;
        this.externalUserMapper = externalUserMapper;
        this.auditPublisher = auditPublisher;
        this.notificationService = notificationService;
        this.documentService = documentService;
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessUser(authentication)")
    public ResponseEntity<ExternalUserAccountDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(externalUserMapper.toDto(externalUserService.find(id)));
    }

    @PostMapping("/account/signup")
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ExternalUserAccountDto externalUserDto) {
        ExternalUserEntity registeredUser = externalUserService.findByEmail(externalUserDto.getEmail()).orElse(null);
        if (registeredUser != null) {
            if (registeredUser.isEnabled()) {
                registeredUser = externalUserService.recoverExternalUserAccountPassword(registeredUser.getEmail()).get();
                notificationService.createExternalUserAlreadyRegisteredNotification(registeredUser);
            } else {
                notificationService.createExternalUserRegistrationAlreadyRequested(registeredUser);
            }
        } else {
            ExternalUserEntity user = externalUserService.create(externalUserMapper.toEntity(externalUserDto));
            notificationService.createRegisterExternalUserAccountNotification(user);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/signup/confirm")
    @Timed
    public ResponseEntity<ExternalUserAccountDto> create(@QueryParam("token") String token) throws URISyntaxException {
        ExternalUserEntity externalUser = externalUserService.confirmUserRegistration(token);
        return ResponseEntity.created(new URI("/api/account/signup/" + externalUser.getEmail())).body(externalUserMapper.toDto(externalUser));
    }

    @PostMapping("/account/logout")
    @Timed
    public ResponseEntity<String> logout(HttpServletRequest request) {
        externalUserService.logout(SecurityUtils.resolveToken(request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<ExternalUserAccountBaseDto> getAccount() {
        ExternalUserEntity databaseUser = externalUserService.getUsuarioWithAuthorities();
        return new ResponseEntity<>(databaseUser != null ? externalUserMapper.toDto(databaseUser) : null, HttpStatus.OK);
    }

    @PutMapping("/account")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<ExternalUserAccountBaseDto> update(@Valid @RequestBody ExternalUserAccountBaseDto userDto) {
        Optional<ExternalUserEntity> existingUser = externalUserRepository.findOneByEmail(userDto.getEmail().toLowerCase());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDto.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, ErrorConstants.USUARIO_EXISTE, ErrorMessagesConstants.USUARIO_EXISTE)).body(null);
        }

        ExternalUserEntity user = externalUserService.update(externalUserMapper.baseDtoToEntity(userDto));
        Optional<ExternalUserAccountBaseDto> updatedUser = Optional.ofNullable(externalUserMapper.toDto(user));

        auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, userDto.getEmail());
        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    @DeleteMapping("/account/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canDeleteUser(authentication)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            externalUserService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_DELETE_FAILED);
        }
    }

    @PostMapping("/account/change-password")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto passwordDto) {
        ExternalUserEntity user = externalUserService.getUsuarioWithAuthorities();
        externalUserService.updateExternalUserAccountPassword(user, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());

        ExternalUserAccountBaseDto updatedUserDto = externalUserMapper.toBaseDto(user);

        auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, updatedUserDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{id}/document/my-information")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<Void> downloadMyInformation(@PathVariable Long id, HttpServletResponse response) {
        try {
            byte[] documento = documentService.generatePdfExternalUserInformation(id);

            DocumentUtil.download(documento, DocumentConstants.DOC_EXTERNAL_USER_INFORMATION, response);
            auditPublisher.publish(AuditConstants.EXT_USUARIO_DOWNLOAD_DOCUMENT, DocumentConstants.EXTERNAL_USER_INFORMATION);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new EDatosException(ServiceExceptionType.GENERIC_ERROR);
        }
    }

}
