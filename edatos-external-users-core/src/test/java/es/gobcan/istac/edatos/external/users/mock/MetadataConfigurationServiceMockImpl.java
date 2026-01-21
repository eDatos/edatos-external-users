package es.gobcan.istac.edatos.external.users.mock;

import org.siemac.edatos.common.test.mock.ConfigurationServiceMockImpl;
import org.siemac.edatos.core.common.exception.EDatosException;

import es.gobcan.istac.edatos.external.users.core.config.ExternalUsersConfigurationConstants;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

public class MetadataConfigurationServiceMockImpl extends ConfigurationServiceMockImpl implements MetadataConfigurationService {

    @Override
    public boolean retrieveCaptchaEnable() throws EDatosException {
        return retrievePropertyBoolean(ExternalUsersConfigurationConstants.CAPTCHA_ENABLE);
    }

    @Override
    public String retrieveCaptchaProvider() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_PROVIDER);
    }

    @Override
    public String retrieveRecaptchaSiteKey() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_RECAPTCHA_SITE_KEY);
    }

    @Override
    public String retrieveRecaptchaV2SiteKey() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_RECAPTCHA_V2_SITE_KEY);
    }

    @Override
    public double retrieveRecaptchaThreshold() throws EDatosException {
        return Double.parseDouble(retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_RECAPTCHA_THRESHOLD));
    }

    @Override
    public String retrieveRecaptchaCredentials() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_RECAPTCHA_CREDENTIALS);
    }

    @Override
    public String retrieveRecaptchaProjectName() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CAPTCHA_RECAPTCHA_PROJECT_NAME);
    }

    @Override
    public String retrieveBaseUrl() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.BASE_URL);
    }

    @Override
    public String retrieveCronExpressionSendNoticeJob() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.CRON_EXPRESSION_SEND_NOTICE_JOB);
    }

    @Override
    public String retrieveNationalityUrnKey() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.NATIONALITY_URN_KEY);
    }

    @Override
    public String retrieveGenderUrnKey() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.GENDER_URN_KEY);
    }

    @Override
    public String retrieveNationalityRestriction() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.NATIONALITY_RESTRICTION);
    }

    @Override
    public String retrieveGenderRestriction() throws EDatosException {
        return retrieveProperty(ExternalUsersConfigurationConstants.GENDER_RESTRICTION);
    }

    @Override
    public boolean retrieveSimplifiedSignUp() throws EDatosException {
        return retrievePropertyBoolean(ExternalUsersConfigurationConstants.SIMPLIFIED_SIGN_UP);
    }
}
