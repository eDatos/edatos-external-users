package es.gobcan.istac.edatos.external.users.job;

import es.gobcan.istac.edatos.external.users.core.repository.InternalEnabledTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;

@Component
public class DeleteExpiredInternalEnabledTokens {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredInternalEnabledTokens.class);
    private final InternalEnabledTokenRepository internalEnabledTokenRepository;

    public DeleteExpiredInternalEnabledTokens(InternalEnabledTokenRepository internalEnabledTokenRepository) {
        this.internalEnabledTokenRepository = internalEnabledTokenRepository;
    }

    @Transactional
    @Scheduled(cron = "${application.jobs.cron.enabledTokens}")
    public void execute() {
        LOG.info("Starting to clean expired internal enabled tokens...");
        internalEnabledTokenRepository.deleteByExpirationDateBefore(Instant.now());
        LOG.info("All expired internal enabled tokens deleted");
    }
}
