package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.siemac.edatos.core.common.exception.CommonServiceExceptionType;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorMessagesConstants;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalUserMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.HeaderUtil;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

import static es.gobcan.istac.edatos.external.users.rest.internal.resources.ExternalUserResource.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
public class ExternalUserResource extends AbstractResource {

    public static final String BASE_URL = "/api/external-users";

    private static final String ENTITY_NAME = "externalUser";

    private final ExternalUserRepository externalUserRepository;
    private final ExternalUserService externalUserService;
    private final ExternalUserMapper externalUserMapper;
    private final NotificationService notificationService;
    private final AuditEventPublisher auditPublisher;

    public ExternalUserResource(ExternalUserRepository externalUserRepository, ExternalUserService externalUserService, ExternalUserMapper externalUserMapper, AuditEventPublisher auditPublisher,
            NotificationService notificationService) {
        this.externalUserRepository = externalUserRepository;
        this.externalUserService = externalUserService;
        this.externalUserMapper = externalUserMapper;
        this.auditPublisher = auditPublisher;
        this.notificationService = notificationService;
    }

    @Timed
    @PostMapping
    @PreAuthorize("@secChecker.canCreateUser(authentication)")
    public ResponseEntity<ExternalUserDto> create(@Valid @RequestBody ExternalUserDto externalUser) throws URISyntaxException {
        if (externalUser.getId() != null) {
            throw new EDatosException(CommonServiceExceptionType.PARAMETER_UNEXPECTED, "id");
        }

        if (externalUserRepository.findOneByEmail(externalUser.getEmail().toLowerCase()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).headers(HeaderUtil.createFailureAlert(ENTITY_NAME, ErrorConstants.USUARIO_EXISTE, ErrorMessagesConstants.USUARIO_EXISTE)).body(null);
        }

        ExternalUserEntity userEntity = externalUserService.create(externalUserMapper.toEntity(externalUser));
        notificationService.createNewExternalUserAccountNotification(userEntity);
        ExternalUserDto userDto = externalUserMapper.toDto(userEntity);

        auditPublisher.publish(AuditConstants.EXT_USUARIO_CREACION, userDto.getEmail());
        return ResponseEntity.created(new URI(BASE_URL + SLASH + userDto.getId())).body(userDto);
    }

    @Timed
    @PutMapping
    @PreAuthorize("@secChecker.canUpdateUser(authentication)")
    public ResponseEntity<ExternalUserDto> update(@Valid @RequestBody ExternalUserDto userDto) {
        Optional<ExternalUserEntity> existingUser = externalUserRepository.findOneByEmail(userDto.getEmail().toLowerCase());

        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDto.getId()))) {
            // login already exists, there cannot be two users with the same login
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, ErrorConstants.USUARIO_EXISTE, ErrorMessagesConstants.USUARIO_EXISTE)).body(null);
        }

        ExternalUserEntity user = externalUserService.update(externalUserMapper.toEntity(userDto));
        Optional<ExternalUserDto> updatedUser = Optional.ofNullable(externalUserMapper.toDto(user));

        auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, userDto.getEmail());
        return ResponseUtil.wrapOrNotFound(updatedUser);
    }

    @Timed
    @GetMapping
    @PreAuthorize("@secChecker.canAccessUser(authentication)")
    public ResponseEntity<List<ExternalUserDto>> find(Pageable pageable, Boolean includeDeleted, String query) {
        Page<ExternalUserDto> page = externalUserService.find(pageable, includeDeleted, query).map(externalUserMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, BASE_URL);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secChecker.canAccessUser(authentication)")
    public ResponseEntity<ExternalUserDto> getExternalUserById(@PathVariable Long id) {
        return ResponseEntity.ok(externalUserMapper.toDto(externalUserService.find(id)));
    }

    @Timed
    @DeleteMapping("/{id}")
    @PreAuthorize("@secChecker.canModifyUserStatus(authentication)")
    public ResponseEntity<ExternalUserDto> deactivateExternalUser(@PathVariable Long id) {
        ExternalUserDto dto = externalUserMapper.toDto(externalUserService.deactivate(id));
        return ResponseEntity.ok(dto);
    }

    @Timed
    @PutMapping("/{id}/restore")
    @PreAuthorize("@secChecker.canModifyUserStatus(authentication)")
    public ResponseEntity<ExternalUserDto> activateExternalUser(@PathVariable Long id) {
        ExternalUserDto dto = externalUserMapper.toDto(externalUserService.recover(id));
        return ResponseEntity.ok(dto);
    }
}
