package es.gobcan.istac.edatos.external.users.core.job;

import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;

@Component
public class DeleteRegistrationTokens {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteRegistrationTokens.class);
    private final ExternalUserRepository externalUserRepository;

    public DeleteRegistrationTokens(ExternalUserRepository externalUserRepository) {
        this.externalUserRepository = externalUserRepository;
    }

    @Transactional
    @Scheduled(cron = "${application.jobs.cron.registrationTokens}")
    public void execute() {
        LOG.info("Starting to clean user accounts that have not been confirmed for too long...");
        externalUserRepository.deleteAllByEnabledFalseAndCreatedDateIsBefore(Instant.now().minusSeconds(86400));
        LOG.info("All user accounts that have not been confirmed for too long were deleted");
    }
}
