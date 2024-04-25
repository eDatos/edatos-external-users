package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;

import es.gobcan.istac.edatos.external.users.core.domain.AuditEventEntity;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.boot.actuate.audit.AuditEvent;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentConstants;
import es.gobcan.istac.edatos.external.users.core.config.DocumentUtil;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
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
    public ResponseEntity<ExternalUserEntity> create(@Valid @RequestBody ExternalUserAccountDto externalUserDto, @RequestParam(required = false) String token) {
        ExternalUserEntity user = externalUserService.findByEmail(externalUserDto.getEmail()).orElse(null);
        if (user == null) {
            user = externalUserService.create(externalUserMapper.toEntity(externalUserDto));
            notificationService.createRegisterExternalUserAccountNotification(user);
            auditPublisher.publish(AuditConstants.EXT_USER_REGISTRATION, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER, user.getId().toString());
            return ResponseEntity.ok(user);
        }

        if (externalUserService.isMigratedUser(user)) {
            return responseToMigratedUser(externalUserDto, token, user);
        }

        if (user.isEnabled()) {
            user = externalUserService.recoverExternalUserAccountPassword(user.getEmail()).get();
            notificationService.createExternalUserAlreadyRegisteredNotification(user);
        } else {
            notificationService.createExternalUserRegistrationAlreadyRequested(user);
        }
        return ResponseEntity.ok(user);
    }

    private ResponseEntity<ExternalUserEntity> responseToMigratedUser(ExternalUserAccountDto externalUserDto, String token, ExternalUserEntity user) {
        if (user.getRegistrationToken().equals(token)) {
            user = externalUserService.migrate(externalUserMapper.toEntity(externalUserDto));
            auditPublisher.publish(AuditConstants.EXT_USER_MIGRATION_CONFIRM, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER, user.getId().toString());
            return ResponseEntity.ok(user);
        } else {
            notificationService.createMigratedExternalUserPendingRegistrationNotification(user);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/account/signup/confirm")
    @Timed
    public ResponseEntity<ExternalUserAccountDto> create(@QueryParam("token") String token) throws URISyntaxException {
        ExternalUserEntity externalUser = externalUserService.confirmUserRegistration(token);
        auditPublisher.publish(AuditConstants.EXT_USER_CONFIRM_REGISTRATION, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER, externalUser.getId().toString());
        return ResponseEntity.created(new URI("/api/account/signup/" + externalUser.getEmail())).body(externalUserMapper.toDto(externalUser));
    }

    @GetMapping("/account/migrated-user-info")
    @Timed
    public ResponseEntity<ExternalUserAccountDto> getUserInfoByRegistrationToken(@QueryParam("token") String token) {
        ExternalUserEntity externalUser = externalUserService.getByUserRegistration(token);
        return ResponseEntity.ok(externalUserMapper.toDto(externalUser));
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

    @PutMapping("/account/email")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<ExternalUserAccountBaseDto> updateEmail(@RequestBody String email) {
        ExternalUserEntity authenticatedUser = externalUserRepository.findOneByEmailIgnoreCase(SecurityUtils.getCurrentUserLogin()).orElse(null);

        if (SecurityUtils.getCurrentUserLogin().equals(email)) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_EMAIL_IS_THE_SAME);
        }

        Optional<ExternalUserEntity> userFoundByEmail = externalUserRepository.findOneByEmailIgnoreCase(email);
        if (userFoundByEmail.isPresent()) {
            notificationService.createEmailAlreadyInUseOnModificationNotice(userFoundByEmail.get(), authenticatedUser);
            return new ResponseEntity<>(externalUserMapper.toBaseDto(authenticatedUser), HttpStatus.OK);
        }

        ExternalUserEntity userWithEmailKey = externalUserService.createEmailModificationRequest(authenticatedUser, email).orElse(null);
        if (userWithEmailKey == null) {
            throw new EDatosException(ServiceExceptionType.LOGIN_USER_DISABLED);
        }

        notificationService.createEmailModificationNotice(userWithEmailKey);
        auditPublisher.publish(AuditConstants.EXT_USER_EMAIL_UPDATE, AuditPrincipalType.EXTERNAL_USER);
        return new ResponseEntity<>(externalUserMapper.toBaseDto(userWithEmailKey), HttpStatus.OK);
    }

    @GetMapping("/account/email/confirm")
    @Timed
    public ResponseEntity<Void> confirmEmailUpdate(@QueryParam("key") String key) {
        Optional<ExternalUserEntity> user = externalUserService.findByEmailModificationRequestKey(key);
        if (!user.isPresent() || externalUserService.findByEmail(user.get().getEmailModificationRequest().getNewEmail()).isPresent()) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_EMAIL_MODIFICATION_ERROR);
        }

        user = externalUserService.confirmEmailModification(key);
        auditPublisher.publish(AuditConstants.EXT_USER_EMAIL_CONFIRM, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER, user.get().getId().toString());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/account")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<ExternalUserAccountBaseDto> update(@Valid @RequestBody ExternalUserAccountBaseDto userDto) {
        ExternalUserEntity user = externalUserService.update(externalUserMapper.baseDtoToEntity(userDto));
        Optional<ExternalUserAccountBaseDto> updatedUser = Optional.ofNullable(externalUserMapper.toDto(user));

        auditPublisher.publish(AuditConstants.EXT_USER_EDITION, AuditPrincipalType.EXTERNAL_USER);
        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    @DeleteMapping("/account/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canDeleteUser(authentication)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            AuditEvent auditEvent = auditPublisher.createAuditEvent(AuditConstants.EXT_USER_DELETION, AuditPrincipalType.EXTERNAL_USER);
            notificationService.createDeleteExternalUserAccountNotification(externalUserService.delete(id));
            auditPublisher.publish(auditEvent);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_DELETE_FAILED);
        }
    }

    @PostMapping("/account/change-password")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto passwordDto, @RequestParam(required = false) String token) {
        ExternalUserEntity user = externalUserService.getUsuarioWithAuthorities();

        if(!SecurityUtils.passwordEncoderMatches(passwordDto.getCurrentPassword(), user.getPassword())){
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_PASSWORD_CHANGE_FAILED);
        }

        externalUserService.updateExternalUserAccountPassword(user, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());

        auditPublisher.publish(AuditConstants.EXT_USER_PASSWORD_EDITION, AuditPrincipalType.EXTERNAL_USER);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{id}/document/my-information")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<Void> downloadMyInformation(@PathVariable Long id, HttpServletResponse response) {
        byte[] documento = documentService.generatePdfExternalUserInformation(id);

        DocumentUtil.download(documento, DocumentConstants.DOC_EXTERNAL_USER_INFORMATION, response);
        auditPublisher.publish(AuditConstants.EXT_USER_DOWNLOAD_DOCUMENT, AuditPrincipalType.EXTERNAL_USER, DocumentConstants.EXTERNAL_USER_INFORMATION);
        return ResponseEntity.ok().build();
    }

}
