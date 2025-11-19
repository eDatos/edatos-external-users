package es.gobcan.istac.edatos.external.users.core.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceSettings;
import com.google.recaptchaenterprise.v1.*;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.CaptchaResponseEntity;
import es.gobcan.istac.edatos.external.users.core.errors.InternalCaptchaException;
import es.gobcan.istac.edatos.external.users.core.repository.CaptchaResponseRepository;
import es.gobcan.istac.edatos.external.users.core.service.CaptchaService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final Logger log = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Autowired
    private MetadataProperties metadataProperties;

    @Autowired
    private CaptchaResponseRepository captchaResponseRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public void saveResponse(String key, String response) {
        captchaResponseRepository.save(new CaptchaResponseEntity(key, response));
    }

    @Override
    public String getResponse(String key) {
        if(key == null) {
            return null;
        }
        CaptchaResponseEntity captchaResponse = captchaResponseRepository.findOne(key);
        captchaResponseRepository.delete(key);
        return captchaResponse == null ? null : captchaResponse.getResponse();
    }

    @Override
    public boolean validateSimple(String userResponse, String key) {
        return userResponse != null && StringUtils.equals(getResponse(key), userResponse);
    }

    @Override
    public boolean validateRecaptcha(String userResponse, String recaptchaAction) {
        log.debug("Request to validate reCAPTCHA for action: {}", recaptchaAction);

        try {
            RecaptchaEnterpriseServiceSettings recaptchaSettings = createRecaptchaSettings();
            try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create(recaptchaSettings)) {
                Assessment response = createAssessment(client, userResponse, metadataProperties.getRecaptchaSiteKey());

                if (!response.getTokenProperties().getValid()) {
                    TokenProperties.InvalidReason invalidReason = response.getTokenProperties().getInvalidReason();

                    if (invalidReason == TokenProperties.InvalidReason.INVALID_REASON_UNSPECIFIED) {
                        log.debug("Falling back to reCAPTCHA v2 validation");
                        return validateRecaptchaV2(client, userResponse);
                    }

                    log.error("reCAPTCHA token is invalid. Reason: {}", invalidReason);
                    throw new InternalCaptchaException();
                }

                if (!response.getTokenProperties().getAction().equals(recaptchaAction)) {
                    log.error("reCAPTCHA action mismatch. Expected: {}, Received: {}", recaptchaAction, response.getTokenProperties().getAction());
                    throw new InternalCaptchaException();
                }

                float score = response.getRiskAnalysis().getScore();
                double threshold = metadataProperties.getRecaptchaThreshold();
                boolean validationResult = score >= threshold;

                log.debug("reCAPTCHA score: {}, threshold: {}, validation result: {}", score, threshold, validationResult);
                return validationResult;
            }
        } catch (InternalCaptchaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error communicating with reCAPTCHA service: {}", e.getMessage(), e);
            throw new InternalCaptchaException();
        }
    }

    private RecaptchaEnterpriseServiceSettings createRecaptchaSettings() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
            new ByteArrayInputStream(metadataProperties.getRecaptchaCredentials().getBytes())
        );
        return RecaptchaEnterpriseServiceSettings.newBuilder()
            .setCredentialsProvider(() -> googleCredentials)
            .build();
    }

    private Assessment createAssessment(RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient, String token, String siteKey) {
        Event event = createEvent(token, siteKey);
        CreateAssessmentRequest request = createAssessmentRequest(event);
        return recaptchaEnterpriseServiceClient.createAssessment(request);
    }

    private Event createEvent(String token, String siteKey) {
        Event.Builder eventBuilder = Event.newBuilder()
            .setSiteKey(siteKey)
            .setToken(token);

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isEmpty()) {
            eventBuilder.setUserAgent(userAgent);
        }

        String clientIp = getClientIpAddress();
        if (clientIp != null && !clientIp.isEmpty()) {
            eventBuilder.setUserIpAddress(clientIp);
        }

        return eventBuilder.build();
    }

    private String getClientIpAddress() {
        String[] headersToCheck = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private CreateAssessmentRequest createAssessmentRequest(Event event) {
        return CreateAssessmentRequest.newBuilder()
            .setParent(ProjectName.of(metadataProperties.getRecaptchaProjectName()).toString())
            .setAssessment(Assessment.newBuilder().setEvent(event).build())
            .build();
    }

    private boolean validateRecaptchaV2(RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient, String token) {
        Assessment response = createAssessment(recaptchaEnterpriseServiceClient, token, metadataProperties.getRecaptchaV2SiteKey());
        boolean isValid = response.getTokenProperties().getValid();
        log.debug("reCAPTCHA v2 validation result: {}", isValid);
        return isValid;
    }

    @Override
    public boolean validateCaptchaGobcan(String userResponse, String key) {
        return userResponse != null && StringUtils.equals(getResponse(key), userResponse);
    }
}
