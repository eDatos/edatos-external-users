package es.gobcan.istac.edatos.external.users.core.config;

public final class CaptchaConstants {

    // PROVIDERS
    public static final String CAPTCHA_PROVIDER_RECAPTCHA = "recaptcha";
    public static final String CAPTCHA_PROVIDER_SIMPLE = "simple";

    // CONSTANTS FOR ALL PROVIDERS
    public static final String CAPTCHA_INTERNATIONALIZATION_COOKIE_ID = "internationalizationCookieId";
    public static final String CAPTCHA_DEFAULT_LANGUAGE = "defaultLanguage";
    public static final String CAPTCHA_PICTURE_MODEL_ATTR = "captchaPictureUrl";
    public static final String CAPTCHA_ENABLED_MODEL_ATTR = "captchaEnabled";
    public static final long CAPTCHA_LIFESPAN = 1800000;

    // SPECIFIC CONSTANTS FOR RECAPTCHA
    public static final String RECAPTCHA_SITE_KEY_MODEL_ATTR = "recaptchaSiteKey";
    public static final String RECAPTCHA_V2_SITE_KEY_MODEL_ATTR = "recaptchaV2SiteKey";
    public static final String RECAPTCHA_VERIFICATION_API = "https://www.google.com/recaptcha/api/siteverify";

    // SPECIFIC CONSTANTS FOR CAPTCHA SIMPLE
    public static final int CAPTCHA_SIMPLE_WIDTH = 200;
    public static final int CAPTCHA_SIMPLE_HEIGHT = 50;

    private CaptchaConstants() { }
}
