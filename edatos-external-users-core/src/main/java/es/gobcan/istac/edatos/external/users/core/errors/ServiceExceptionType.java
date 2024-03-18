package es.gobcan.istac.edatos.external.users.core.errors;

import org.siemac.edatos.core.common.exception.CommonServiceExceptionType;

public class ServiceExceptionType extends CommonServiceExceptionType {

    public static final CommonServiceExceptionType GENERIC_ERROR = create("exception.external_users.generic");

    /********
     * CATEGORY
     ********/
    public static final CommonServiceExceptionType CANNOT_DELETE_CATEGORY_IF_IT_HAS_SUBSCRIBERS = create("exception.external_users.category.cannot_delete_category_if_it_has_subscribers");
    public static final CommonServiceExceptionType CANNOT_UPDATE_CATEGORY_TREE_IF_MISSING_DEFAULT_TRANSLATION = create("exception.external_users.category.cannot_update_category_tree_if_missing_default_translation");

    /********
     * QUERY
     ********/
    public static final CommonServiceExceptionType QUERY_NOT_SUPPORTED = create("exception.external_users.query.not_supported");

    /********
     * LOGIN
     ********/
    public static final CommonServiceExceptionType LOGIN_USER_DISABLED = create("exception.external_users.login.user_disabled");
    public static final CommonServiceExceptionType LOGIN_VOID = create("exception.external_users.login.void");
    public static final CommonServiceExceptionType LOGIN_FAILED = create("exception.external_users.login.failed");

    /********
     * SIGNUP
     ********/
    public static final CommonServiceExceptionType REGISTER_ACCOUNT_NOTICE_FAILED = create("exception.external_users.register_account.create_notice");
    public static final CommonServiceExceptionType UNSUBSCRIBE_ACCOUNT_NOTICE_FAILED = create("exception.external_users.unsubscribe_account.create_notice");
    public static final CommonServiceExceptionType RESET_PASSWORD_NOTICE_FAILED = create("exception.external_users.reset_password.create_notice");
    public static final CommonServiceExceptionType CONFIRM_SIGNUP_NOTICE_FAILED = create("exception.external_users.confirm_signup.create_notice");
    public static final CommonServiceExceptionType COMPLETE_SIGNUP_NOTICE_FAILED = create("exception.external_users.complete_signup.create_notice");
    public static final CommonServiceExceptionType EMAIL_IN_USE_NOTICE_FAILED = create("exception.external_users.email_in_use.create_notice");
    public static final CommonServiceExceptionType REPEATED_SIGNUP_NOTICE_FAILED = create("exception.external_users.repeated_signup.create_notice");
    public static final CommonServiceExceptionType MIGRATED_USER_SIGNUP_NOTICE_FAILED = create("exception.external_users.migrated_user_signup.create_notice");
    public static final CommonServiceExceptionType SUBSCRIPTION_JOB_NOTICE_FAILED = create("exception.external_users.subscrition_job.create_notice");

    /********
     * EXTERNAL_USER
     ********/
    public static final CommonServiceExceptionType EXTERNAL_USER_DELETE_FAILED = create("exception.external_users.delete.failed");
    public static final CommonServiceExceptionType EXTERNAL_USER_DEACTIVATE = create("exception.external_users.deactivate");
    public static final CommonServiceExceptionType EXTERNAL_USER_DEAUTHENTICATED = create("exception.external_users.deauthenticated");
    public static final CommonServiceExceptionType EXTERNAL_USER_REGISTRATION_CONFIRMATION = create("exception.external_users.registration_confirmation");
    public static final CommonServiceExceptionType EXTERNAL_USER_EMAIL_IS_THE_SAME = create("exception.external_users.email_is_the_same");

    /********
     * PASSWORD
     ********/
    public static final CommonServiceExceptionType PASSWORD_NOT_MATCH = create("exception.external_users.password.not_match");

    /********
     * JASPERREPORTS
     ********/
    public static final CommonServiceExceptionType ERROR_JASPER_REPORTS_GENERATING = create("exception.external_users.jasperreports.generating");

    /********
     * INTERNATIONAL_STRING
     ********/
    public static final CommonServiceExceptionType MISSING_DEFAULT_LOCALE_TRANSLATION = create("exception.external_users.international_string.missing_default_locale_translation");

    /********
     * CAPTCHA
     ********/
    public static final CommonServiceExceptionType CAPTCHA_UNSUCCESSFUL = create("exception.external_users.captcha.unsuccessful");

    /********
     * NOTIFICATION
     ********/

    public static final CommonServiceExceptionType SENDING_NOTICE_FAILED = create("exception.external_users.notice.sending_notice");
    public static final CommonServiceExceptionType EMAIL_MODIFICATION_NOTICE_FAILED = create("exception.external_users.email_modification.create_notice");

    /********
     * FAVORITE
     ********/
    public static final CommonServiceExceptionType FAVORITE_CREATE_ANCESTOR_ALREADY_FAVORITE = create("exception.external_users.favorite.ancestor_already_favorite");

}
