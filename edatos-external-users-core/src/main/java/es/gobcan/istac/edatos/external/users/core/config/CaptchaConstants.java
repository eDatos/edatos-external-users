package es.gobcan.istac.edatos.external.users.core.config;

public final class CaptchaConstants {

    // PROVIDERS
    public static final String CAPTCHA_PROVIDER_GOBCAN = "gobcan";
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

    // SPECIFIC CONSTANTS FOR CAPTCHA SIMPLE
    public static final int CAPTCHA_SIMPLE_WIDTH = 200;
    public static final int CAPTCHA_SIMPLE_HEIGHT = 50;

    // SPECIFIC CONSTANTS FOR CAPTCHA GOBCAN
    public static final int CAPTCHA_GOBCAN_WIDTH = 300;
    public static final int CAPTCHA_GOBCAN_HEIGHT = 100;
    public static final String CAPTCHA_GOBCAN_WSDL_PATH = "/wsdl/captcha-gobcan.wsdl";
    public static final String CAPTCHA_GOBCAN_IMAGE_FORMAT = "jpg";
    public static final String CAPTCHA_GOBCAN_FONTNAME = "Arial Narrow";
    public static final char[] CAPTCHA_GOBCAN_OPERANDS = "0123456789".toCharArray();
    public static final char[] CAPTCHA_GOBCAN_OPERATORS = "+-*".toCharArray();

    private CaptchaConstants() { }
}
