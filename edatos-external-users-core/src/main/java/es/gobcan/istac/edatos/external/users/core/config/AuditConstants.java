package es.gobcan.istac.edatos.external.users.core.config;

public final class AuditConstants {

    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String EVENT_SUBTYPE = "eventSubtype";
    public static final String PRINCIPAL_TYPE = "principalType";

    public static final String EXT_USER_EDITION = "EXT_USER_EDITION";
    public static final String EXT_USER_CONFIRM_REGISTRATION = "EXT_USER_CONFIRM_REGISTRATION";
    public static final String EXT_USER_REGISTRATION = "EXT_USER_REGISTRATION";
    public static final String EXT_USER_DELETION = "EXT_USER_DELETION";
    public static final String EXT_USER_PASSWORD_RECOVER = "EXT_USER_PASSWORD_RECOVER";
    public static final String EXT_USER_PASSWORD_CONFIRM = "EXT_USER_PASSWORD_CONFIRM";
    public static final String EXT_USER_PASSWORD_EDITION = "EXT_USER_PASSWORD_EDITION";
    public static final String EXT_USER_EMAIL_UPDATE = "EXT_USER_EMAIL_UPDATE";
    public static final String EXT_USER_EMAIL_CONFIRM = "EXT_USER_EMAIL_CONFIRM";
    public static final String EXT_USER_MIGRATION_CONFIRM = "EXT_USER_MIGRATION_CONFIRM";

    public static final String EXT_USER_DOWNLOAD_DOCUMENT = "EXT_USER_DOWNLOAD_DOCUMENT";

    public static final String FILTER_CREATION = "FILTER_CREATION";
    public static final String FILTER_EDITION = "FILTER_EDITION";
    public static final String FILTER_DELETION = "FILTER_DELETION";
    public static final String FILTER_LAST_ACCESS_EDITION = "FILTER_LAST_ACCESS_EDITION";

    public static final String FAVORITE_CREATION = "FAVORITE_CREATION";
    public static final String FAVORITE_DELETION = "FAVORITE_DELETION";
    public static final String FAVORITE_OPERATION_SUBTYPE = "FAVORITE_OPERATION_SUBTYPE";
    public static final String FAVORITE_CATEGORY_SUBTYPE = "FAVORITE_CATEGORY_SUBTYPE";

    public static final String EXTERNAL_OPERATION_EDITION = "EXTERNAL_OPERATION_EDITION";

    public static final String CATEGORY_TREE_UPDATE = "CATEGORY_TREE_UPDATE";
    public static final String CATEGORY_UPDATE = "CATEGORY_UPDATE";
    public static final String CATEGORY_CREATION = "CATEGORY_CREATION";
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";

    public static final String DATA_PROTECTION_POLICY_EDITION = "DATA_PROTECTION_POLICY_EDITION";

    private AuditConstants() {
    }
}
