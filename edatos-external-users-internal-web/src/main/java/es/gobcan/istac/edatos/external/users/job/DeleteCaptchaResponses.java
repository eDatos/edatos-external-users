package es.gobcan.istac.edatos.external.users.job;

import java.time.Instant;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.config.CaptchaConstants;
import es.gobcan.istac.edatos.external.users.core.repository.CaptchaResponseRepository;

@Component
public class DeleteCaptchaResponses {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteCaptchaResponses.class);
    private final CaptchaResponseRepository captchaResponseRepository;

    public DeleteCaptchaResponses(CaptchaResponseRepository captchaResponseRepository) {
        this.captchaResponseRepository = captchaResponseRepository;
    }

    @Transactional
    @Scheduled(cron = "${application.jobs.cron.captchaResponses}")
    public void execute() {
        Long numberOfDeletedRows = captchaResponseRepository.deleteByCreatedDateBefore(Instant.now().minusMillis(CaptchaConstants.CAPTCHA_LIFESPAN));
        if (numberOfDeletedRows > 0) {
            LOG.info("All captcha responses that have existed for too long (" + numberOfDeletedRows + ") were deleted");
        }
    }
}
