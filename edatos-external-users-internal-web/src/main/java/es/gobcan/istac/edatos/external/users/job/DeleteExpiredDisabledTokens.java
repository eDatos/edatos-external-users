package es.gobcan.istac.edatos.external.users.job;

import java.time.Instant;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.service.DisabledTokenService;

@Component
public class DeleteExpiredDisabledTokens {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredDisabledTokens.class);
    private final DisabledTokenService disabledTokenService;

    public DeleteExpiredDisabledTokens(DisabledTokenService disabledTokenService) {
        this.disabledTokenService = disabledTokenService;
    }

    @Transactional
    @Scheduled(cron = "${application.jobs.cron.disabledTokens}")
    public void execute() {
        LOG.info("Starting to clean expired disabled tokens...");
        disabledTokenService.deleteByExpirationDateBefore(Instant.now());
        LOG.info("All expired disabled tokens deleted");
    }
}
