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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private MetadataProperties metadataProperties;

    @Autowired
    private CaptchaResponseRepository captchaResponseRepository;

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
        try {
            RecaptchaEnterpriseServiceSettings recaptchaSettings = createRecaptchaSettings();
            try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create(recaptchaSettings)) {
                Assessment response = createAssessment(client, userResponse, metadataProperties.getRecaptchaSiteKey());
                if (!response.getTokenProperties().getValid()) {
                    if (response.getTokenProperties().getInvalidReason() == TokenProperties.InvalidReason.INVALID_REASON_UNSPECIFIED) {
                        return validateRecaptchaV2(client, userResponse);
                    } else {
                        throw new InternalCaptchaException();
                    }
                }

                if (!response.getTokenProperties().getAction().equals(recaptchaAction)) {
                    throw new InternalCaptchaException();
                }

                return response.getRiskAnalysis().getScore() >= metadataProperties.getRecaptchaThreshold();
            }
        } catch (IOException e) {
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
        return Event.newBuilder()
            .setSiteKey(siteKey)
            .setToken(token)
            .build();
    }

    private CreateAssessmentRequest createAssessmentRequest(Event event) {
        return CreateAssessmentRequest.newBuilder()
            .setParent(ProjectName.of(metadataProperties.getRecaptchaProjectName()).toString())
            .setAssessment(Assessment.newBuilder().setEvent(event).build())
            .build();
    }

    private boolean validateRecaptchaV2(RecaptchaEnterpriseServiceClient recaptchaEnterpriseServiceClient, String token) {
        Assessment response = createAssessment(recaptchaEnterpriseServiceClient, token, metadataProperties.getRecaptchaV2SiteKey());
        if (!response.getTokenProperties().getValid()) {
            throw new InternalCaptchaException();
        }
        return true;
    }

    @Override
    public boolean validateCaptchaGobcan(String userResponse, String key) {
        return userResponse != null && StringUtils.equals(getResponse(key), userResponse);
    }
}
