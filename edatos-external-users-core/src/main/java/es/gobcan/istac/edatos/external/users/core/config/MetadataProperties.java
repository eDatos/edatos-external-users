package es.gobcan.istac.edatos.external.users.core.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Component("metadataProperties")
public class MetadataProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataProperties.class);

    @Autowired
    private MetadataConfigurationService configurationService;

    private String metamacNavbar;
    private String metamacCasPrefix;
    private String metamacCasLoginUrl;
    private String metamacCasLogoutUrl;
    private String defaultLanguage;
    private List<String> availableLanguages;

    private List<String> internationalizationLanguages;
    private String       languageCookie;
    private boolean simplifiedSignUp;


    private boolean captchaEnable;
    private String captchaProvider;
    private String recaptchaSiteKey;
    private String recaptchaSecretKey;
    private String recaptchaV2SiteKey;
    private String recaptchaV2SecretKey;
    private double recaptchaThreshold;
    private String captchaUrl;

    private String externalAppUrl;
    private String internalAppUrl;
    private String casService;

    private String srmExternalEndpoint;

    private String visualizerPath;
    private String siePath;

    private String nationalityUrnKey;
    private String genderUrnKey;
    private String nationalityRestriction;
    private String genderRestriction;

    private List<String> allowedOrigins;

    private String externalOperationsWebUrl;
    private String apiKey;

    @PostConstruct
    public void setValues() {
        metamacNavbar = normalizeUrl(configurationService.retrieveNavbarUrl());
        metamacCasPrefix = normalizeUrl(configurationService.retrieveSecurityCasServerUrlPrefix());
        metamacCasLoginUrl = normalizeUrl(configurationService.retrieveSecurityCasServiceLoginUrl());
        metamacCasLogoutUrl = normalizeUrl(configurationService.retrieveSecurityCasServiceLogoutUrl());
        captchaEnable = configurationService.retrieveCaptchaEnable();
        captchaProvider = configurationService.retrieveCaptchaProvider();
        if (CaptchaConstants.CAPTCHA_PROVIDER_RECAPTCHA.equals(captchaProvider)) {
            recaptchaSiteKey = configurationService.retrieveRecaptchaSiteKey();
            recaptchaSecretKey = configurationService.retrieveRecaptchaSecretKey();
            recaptchaThreshold = configurationService.retrieveRecaptchaThreshold();
            recaptchaV2SiteKey = configurationService.retrieveRecaptchaV2SiteKey();
            recaptchaV2SecretKey = configurationService.retrieveRecaptchaV2SecretKey();
        }
        captchaUrl = normalizeUrl(configurationService.retrieveCaptchaExternalApiUrlBase());
        availableLanguages = configurationService.retrieveLanguages();
        internationalizationLanguages = configurationService.retrieveInternationalizationLanguages();
        externalAppUrl = normalizeUrl(configurationService.retrieveExternalUsersExternalWebApplicationUrlBase());
        internalAppUrl = normalizeUrl(configurationService.retrieveExternalUsersInternalWebApplicationUrlBase());
        casService = internalAppUrl + "/login/cas";
        visualizerPath = configurationService.retrievePortalExternalWebApplicationUrlVisualizer();
        defaultLanguage = availableLanguages.get(0);
        nationalityUrnKey = configurationService.retrieveNationalityUrnKey();
        genderUrnKey = configurationService.retrieveGenderUrnKey();
        nationalityRestriction = configurationService.retrieveNationalityRestriction();
        genderRestriction = configurationService.retrieveGenderRestriction();
        srmExternalEndpoint = configurationService.retrieveSrmExternalApiUrlBase();
        externalOperationsWebUrl = configurationService.retrieveStatisticalOperationsExternalWebApplicationUrlBase();
        try {
            languageCookie = configurationService.retrieveInternationalizationCookieId();
        } catch (Exception e) {
            languageCookie = null;
        }
        try {
            siePath = configurationService.retrieveSieExternalWebApplicationUrl();
        } catch (EDatosException e) {
            LOGGER.info("Optional property SieExternalWebApplicationUrl is not filled ");
        }
        try {
            apiKey = configurationService.retrieveApiKey();
            LOGGER.info("Property ExternalUsersApiKey is " + apiKey);
        } catch (EDatosException e) {
            LOGGER.info("Property ExternalUsersApiKey is not filled ");
        }
        allowedOrigins = configurationService.retrieveAllowedOrigins();
        simplifiedSignUp = configurationService.retrieveSimplifiedSignUp();
    }

    public String getMetamacNavbar() {
        return metamacNavbar;
    }

    public String getMetamacCasPrefix() {
        return metamacCasPrefix;
    }

    public String getMetamacCasLoginUrl() {
        return metamacCasLoginUrl;
    }

    public String getMetamacCasLogoutUrl() {
        return metamacCasLogoutUrl;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public boolean isCaptchaEnable() {
        return captchaEnable;
    }

    public String getCaptchaProvider() {
        return captchaProvider;
    }

    public String getRecaptchaSiteKey() {
        return recaptchaSiteKey;
    }

    public String getRecaptchaSecretKey() {
        return recaptchaSecretKey;
    }

    public String getRecaptchaV2SiteKey() {
        return recaptchaV2SiteKey;
    }

    public String getRecaptchaV2SecretKey() {
        return recaptchaV2SecretKey;
    }

    public String getCaptchaUrl() {
        return captchaUrl;
    }

    public String getExternalAppUrl() {
        return externalAppUrl;
    }

    public String getInternalAppUrl() {
        return internalAppUrl;
    }

    public String getCasService() {
        return casService;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public String getLanguageCookie() {
        return languageCookie;
    }

    private String normalizeUrl(String url) {
        return StringUtils.removeEnd(url, "/");
    }

    public String getVisualizerPath() {
        return visualizerPath;
    }

    public String getSiePath() {
        return siePath;
    }

    public String getNationalityUrnKey() {
        return nationalityUrnKey;
    }

    public String getSrmExternalEndpoint() {
        return srmExternalEndpoint;
    }

    public String getGenderUrnKey() {
        return genderUrnKey;
    }

    public String getNationalityRestriction() {
        return nationalityRestriction;
    }

    public String getGenderRestriction() {
        return genderRestriction;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public double getRecaptchaThreshold() {
        return recaptchaThreshold;
    }

    public List<String> getInternationalizationLanguages() {
        return internationalizationLanguages;
    }

    public String getExternalOperationsWebUrl() {
        return externalOperationsWebUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean getSimplifiedSignUp() {
        return simplifiedSignUp;
    }
}
