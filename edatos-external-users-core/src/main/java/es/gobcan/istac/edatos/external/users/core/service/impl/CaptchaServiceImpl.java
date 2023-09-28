package es.gobcan.istac.edatos.external.users.core.service.impl;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.gobcan.istac.edatos.external.users.core.config.CaptchaConstants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.CaptchaResponseEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.RecaptchaVerificationVO;
import es.gobcan.istac.edatos.external.users.core.errors.CaptchaClientError;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
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
            if (recaptchaVerification.hasClientError()) {
                throw new CaptchaClientError();
            } else if (Arrays.asList(recaptchaVerification.getErrorCodes()).contains(RecaptchaVerificationVO.ErrorCode.INVALID_KEYS)) {
                urlBuilder.replaceQueryParam("secret", "6LeMsVQoAAAAAHvsLBdfokM0Yqi7C9gwHmwMMJOw");
                RecaptchaVerificationVO recaptchaV2Verification = restTemplate.getForEntity(urlBuilder.build(), RecaptchaVerificationVO.class).getBody();
                if (recaptchaV2Verification.hasClientError()) {
                    throw new CaptchaClientError();
                } else if (recaptchaV2Verification.getErrorCodes() != null && recaptchaV2Verification.getErrorCodes().length > 0) {
                    throw new EDatosException(ServiceExceptionType.GENERIC_ERROR);
                }
                return recaptchaV2Verification.wasASuccess();
            } else {
                throw new EDatosException(ServiceExceptionType.GENERIC_ERROR);
            }
        } else if (!recaptchaVerification.getAction().equals(captchaAction)) {
            throw new EDatosException(ServiceExceptionType.GENERIC_ERROR);
        }

        return recaptchaVerification.getScore() >= metadataProperties.getRecaptchaThreshold();
    }

    @Override
    public boolean validateCaptchaGobcan(String userResponse, String key) {
        return userResponse != null && StringUtils.equals(getResponse(key), userResponse);
    }
}
