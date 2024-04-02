package es.gobcan.istac.edatos.external.users.rest.external.resources;

import com.codahale.metrics.annotation.Timed;
import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.KeyAndPasswordDto;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(PasswordResource.BASE_URL)
public class PasswordResource extends AbstractResource {

    public static final String BASE_URL = "/api/recover-password";

    private final ExternalUserService externalUserService;
    private final NotificationService notificationService;

    private final AuditEventPublisher auditPublisher;

    public PasswordResource(ExternalUserService externalUserService, AuditEventPublisher auditPublisher, NotificationService notificationService) {
        this.externalUserService = externalUserService;
        this.auditPublisher = auditPublisher;
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

            auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    @Timed
    public ResponseEntity<Void> finishPasswordReset(@RequestBody KeyAndPasswordDto keyAndPassword) {
        try {
            externalUserService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

            auditPublisher.publish(AuditConstants.EXT_USUARIO_EDICION, AuditPrincipalType.ANONYMOUS_EXTERNAL_USER);
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
