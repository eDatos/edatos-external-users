package es.gobcan.istac.edatos.external.users.web.config;

public final class QuartzConstants {

    public static final String APPLICATION_CONTEXT_KEY = "applicationContext";

    public static final String NOTICE_ID_JOB_DATA = "noticeId";

    public static final String SPRING_PROFILE_DISABLE_QUARTZ = "quartz";

    public static final String DATASETS_SUBSCRIPTIONS_JOB = "sendNoticeOfSubscriptionsJob";
    public static final String DATASETS_SUBSCRIPTIONS_JOB_DESCRIPTION = "Send notifications of updated dataset subscriptions Job";
    public static final String DATASETS_SUBSCRIPTIONS_TRIGGER = "sendNoticeOfSubscriptionsTrigger";

    public static final String DELETE_NOT_MIGRATED_USERS_JOB = "deleteNotMigratedUsersJob";
    public static final String DELETE_NOT_MIGRATED_USERS_DESCRIPTION = "Delete not migrated users Job";
    public static final String DELETE_NOT_MIGRATED_USERS_TRIGGER = "deleteNotMigratedUsersTrigger";

    private QuartzConstants() {
    }

}
