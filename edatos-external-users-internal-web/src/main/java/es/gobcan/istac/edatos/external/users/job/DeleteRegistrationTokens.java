package es.gobcan.istac.edatos.external.users.job;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

import static es.gobcan.istac.edatos.external.users.core.service.impl.ExternalUserServiceImpl.MIGRATED_USER_PSWD;

@Component
public class DeleteRegistrationTokens {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteRegistrationTokens.class);
    private final ExternalUserRepository externalUserRepository;
    private final AuditEventPublisher auditPublisher;

    public DeleteRegistrationTokens(ExternalUserRepository externalUserRepository, AuditEventPublisher auditPublisher) {
        this.externalUserRepository = externalUserRepository;
        this.auditPublisher = auditPublisher;
    }

    @Transactional
    @Scheduled(cron = "${application.jobs.cron.registrationTokens}")
    public void execute() {
        LOG.info("Starting to clean user accounts that have not been confirmed for too long...");
        List<ExternalUserEntity> users = externalUserRepository.deleteAllByEnabledFalseAndPasswordIsNotAndCreatedDateIsBefore(MIGRATED_USER_PSWD, Instant.now().minusSeconds(86400));
        users.forEach(user -> auditPublisher.publish(AuditConstants.EXT_USER_REGISTRATION_UNFINISHED_DELETION, AuditPrincipalType.ADMIN, user.getId().toString()));
        LOG.info("All user accounts that have not been confirmed for too long were deleted");
    }
}
