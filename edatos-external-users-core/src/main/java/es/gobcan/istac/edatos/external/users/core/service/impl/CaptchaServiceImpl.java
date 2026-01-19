package es.gobcan.istac.edatos.external.users.core.service.impl;

import javax.ws.rs.core.UriBuilder;

import es.gobcan.istac.edatos.external.users.core.errors.InternalCaptchaException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.gobcan.istac.edatos.external.users.core.config.CaptchaConstants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.CaptchaResponseEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.RecaptchaVerificationVO;
import es.gobcan.istac.edatos.external.users.core.repository.CaptchaResponseRepository;
import es.gobcan.istac.edatos.external.users.core.service.CaptchaService;

import java.util.Arrays;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private RestTemplate restTemplate = new RestTemplate();

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
    public boolean validateRecaptcha(String userResponse, String captchaAction) {
        UriBuilder urlBuilder = UriBuilder.fromUri(CaptchaConstants.RECAPTCHA_VERIFICATION_API);
        urlBuilder.queryParam("secret", metadataProperties.getRecaptchaSecretKey());
        urlBuilder.queryParam("response", userResponse);

        ResponseEntity<RecaptchaVerificationVO> recaptchaVerificationResponse = restTemplate.getForEntity(urlBuilder.build(), RecaptchaVerificationVO.class);
        RecaptchaVerificationVO recaptchaVerification = recaptchaVerificationResponse.getBody();

        if (!recaptchaVerification.wasASuccess()) {
            if (Arrays.asList(recaptchaVerification.getErrorCodes()).contains(RecaptchaVerificationVO.ErrorCode.INVALID_KEYS)) {
                urlBuilder.replaceQueryParam("secret", metadataProperties.getRecaptchaV2SecretKey());
                RecaptchaVerificationVO recaptchaV2Verification = restTemplate.getForEntity(urlBuilder.build(), RecaptchaVerificationVO.class).getBody();
                if (!recaptchaV2Verification.hasClientError() && recaptchaV2Verification.getErrorCodes() != null && recaptchaV2Verification.getErrorCodes().length > 0) {
                    throw new InternalCaptchaException();
                }
                return recaptchaV2Verification.wasASuccess();
            } else if (!recaptchaVerification.hasClientError() && recaptchaVerification.getErrorCodes() != null && recaptchaVerification.getErrorCodes().length > 0) {
                throw new InternalCaptchaException();
            }
        } else if (!recaptchaVerification.getAction().equals(captchaAction)) {
            throw new InternalCaptchaException();
        }

        return recaptchaVerification.getScore() != null && recaptchaVerification.getScore() >= metadataProperties.getRecaptchaThreshold();
    }

}
