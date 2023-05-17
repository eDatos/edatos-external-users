package es.gobcan.istac.edatos.external.users.core.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Component("metadataProperties")
public class MetadataProperties {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MetadataConfigurationService configurationService;

    private String metamacNavbar;
    private String metamacCasPrefix;
    private String metamacCasLoginUrl;
    private String metamacCasLogoutUrl;
    private String defaultLanguage;
    private List<String> availableLanguages;
    private String languageCookie;

    private boolean captchaEnable;
    private String captchaProvider;
    private String recaptchaSiteKey;
    private String recaptchaSecretKey;
    private String captchaUrl;

    private String externalAppUrl;
    private String internalAppUrl;
    private String casService;

    private String visualizerPath;

    private List<String> allowedOrigins;

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
        }
        captchaUrl = normalizeUrl(configurationService.retrieveCaptchaExternalApiUrlBase());
        availableLanguages = configurationService.retrieveLanguages();
        externalAppUrl = normalizeUrl(configurationService.retrieveExternalUsersExternalWebApplicationUrlBase());
        internalAppUrl = normalizeUrl(configurationService.retrieveExternalUsersInternalWebApplicationUrlBase());
        casService = internalAppUrl + "/login/cas";
        visualizerPath = configurationService.retrievePortalExternalWebApplicationUrlVisualizer();
        defaultLanguage = availableLanguages.get(0);
        try {
            languageCookie = configurationService.retrieveInternationalizationCookieId();
        } catch (Exception e) {
            languageCookie = null;
        }
        allowedOrigins = configurationService.retrieveAllowedOrigins();
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
        // The languages are retrieved each time it is needed so that multi-language fields can be updated
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

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
}
