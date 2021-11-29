package es.gobcan.istac.edatos.external.users.core.job;

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
    @Scheduled(cron = "0 0 3 * * *") // Every day at 03:00
    public void execute() {
        LOG.info("Starting to clean expired disabled tokens...");
        disabledTokenService.deleteByExpirationDateBefore(Instant.now());
        LOG.info("All expired disabled tokens deleted");
    }
}
