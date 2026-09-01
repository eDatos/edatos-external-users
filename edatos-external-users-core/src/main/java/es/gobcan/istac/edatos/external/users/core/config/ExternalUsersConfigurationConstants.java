package es.gobcan.istac.edatos.external.users.core.config;

import org.siemac.edatos.core.common.constants.shared.ConfigurationConstants;

public final class ExternalUsersConfigurationConstants extends ConfigurationConstants {

    // CAPTCHA PROPERTIES
    // These properties are only necessary in this module because they define the behavior of the captcha.
    // Only the captcha URL property is added in ConfigurationConstants. All these properties begin with
    // portal because at the time the captcha was implemented in that module.
    public static final String CAPTCHA_ENABLE = "metamac.portal.rest.external.authentication.captcha.enable";
    public static final String CAPTCHA_PROVIDER = "metamac.portal.rest.external.authentication.captcha.provider";
    public static final String CAPTCHA_RECAPTCHA_SITE_KEY = "metamac.portal.rest.external.authentication.captcha.public_key";
    public static final String CAPTCHA_RECAPTCHA_V2_SITE_KEY = "metamac.portal.rest.external.authentication.captcha.v2.public_key";
    public static final String CAPTCHA_RECAPTCHA_THRESHOLD = "metamac.portal.rest.external.authentication.captcha.threshold";
    public static final String CAPTCHA_RECAPTCHA_CREDENTIALS = "metamac.portal.rest.external.authentication.captcha.credentials";
    public static final String CAPTCHA_RECAPTCHA_PROJECT_NAME = "metamac.portal.rest.external.authentication.captcha.project_name";

    // OTHER PROPERTIES
    public static final String BASE_URL = "metamac.edatos_external_users.rest.external.base";
    public static final String CRON_EXPRESSION_SEND_NOTICE_JOB = "metamac.edatos_external_users.rest.internal.job.send_notice.cron_expression";
    public static final String NATIONALITY_URN_KEY = "metamac.default.codelist.nationality.urn";
    public static final String GENDER_URN_KEY = "metamac.default.codelist.gender.urn";
    public static final String NATIONALITY_RESTRICTION = "metamac.default.codelist.nationality.restriction";
    public static final String GENDER_RESTRICTION = "metamac.default.codelist.gender.restriction";
    public static final String SIMPLIFIED_SIGN_UP = "metamac.edatos_external_users.simplifiedSignUp";

    // API KEYS
    public static final String EXTERNAL_USERS_REST_API_KEY     = "edatos.external_users.rest.api_key";
    public static final String EXTERNAL_USERS_REST_INTERNAL_API_KEY = "edatos.external_users.rest.internal.api_key";

    private ExternalUsersConfigurationConstants() {

    }
}
