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
    public static final String CAPTCHA_RECAPTCHA_SECRET_KEY = "metamac.portal.rest.external.authentication.captcha.private_key";
    public static final String CAPTCHA_RECAPTCHA_THRESHOLD = "metamac.portal.rest.external.authentication.captcha.threshold";

    // OTHER PROPERTIES
    public static final String BASE_URL = "metamac.edatos_external_users.rest.external.base";
    public static final String CRON_EXPRESSION_SEND_NOTICE_JOB = "metamac.edatos_external_users.rest.internal.job.send_notice.cron_expression";

    private ExternalUsersConfigurationConstants() {

    }
}
