package es.gobcan.istac.edatos.external.users.rest.external.resources;

import javax.validation.Valid;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountBaseDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.KeyAndPasswordDto;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalUserAccountMapper;

import java.util.Optional;

@RestController
@RequestMapping(PasswordResource.BASE_URL)
public class PasswordResource extends AbstractResource {

    public static final String BASE_URL = "/api/recover-password";

    private final ExternalUserService externalUserService;
    private final NotificationService notificationService;

    private final ExternalUserAccountMapper externalUserMapper;
    private final AuditEventPublisher auditPublisher;
    private final MessageSource messageSource;

    public PasswordResource(ExternalUserService externalUserService, ExternalUserAccountMapper externalUserMapper, AuditEventPublisher auditPublisher, MessageSource messageSource,
            NotificationService notificationService) {
        this.externalUserService = externalUserService;
        this.externalUserMapper = externalUserMapper;
        this.auditPublisher = auditPublisher;
        this.messageSource = messageSource;
        this.notificationService = notificationService;
    }

    @PostMapping
    @Timed
    public ResponseEntity<Void> recoverPassword(@Valid @RequestBody String email) {
        ExternalUserEntity user = externalUserService.findByEmail(email).orElse(null);
        if (user != null && externalUserService.isMigratedUser(user)) {
            notificationService.createResetPasswordMigratedUserNotification(user);
            return ResponseEntity.ok().build();
        }

        user = externalUserService.recoverExternalUserAccountPassword(email).orElse(null);
        if (user != null) {
            notificationService.createResetPasswordExternaluserAccountNotification(user);

            auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, AuditPrincipalType.EXTERNAL_USER);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    @Timed
    public ResponseEntity<Void> finishPasswordReset(@RequestBody KeyAndPasswordDto keyAndPassword) {
        try {
            externalUserService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

            auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, AuditPrincipalType.EXTERNAL_USER);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new EDatosException(ServiceExceptionType.GENERIC_ERROR);
        }
    }

    @GetMapping("/key-recovery/{key}")
    @Timed
    public ResponseEntity<Boolean> existsResetKey(@PathVariable String key) {
        return ResponseEntity.ok().body(externalUserService.findOneByResetKey(key).isPresent());
    }
}
