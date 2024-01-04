package es.gobcan.istac.edatos.external.users.web.job;

import es.gobcan.istac.edatos.external.users.core.errors.CustomParameterizedExceptionBuilder;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorMessagesConstants;
import es.gobcan.istac.edatos.external.users.core.service.*;
import es.gobcan.istac.edatos.external.users.web.config.QuartzConstants;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractConsumerQuartzJob extends QuartzJobBean {

    protected PlatformTransactionManager getPlatformTransactionManager(JobExecutionContext context) {
        return getApplicationContext(context).getBean(PlatformTransactionManager.class);
    }

    private ApplicationContext getApplicationContext(JobExecutionContext context) {
        try {
            return (ApplicationContext) context.getScheduler().getContext().get(QuartzConstants.APPLICATION_CONTEXT_KEY);
        } catch (SchedulerException e) {
            //@formatter:off
            throw new CustomParameterizedExceptionBuilder()
                .message(ErrorMessagesConstants.QUARTZ_JOB_EXECUTION_ERROR)
                .cause(e)
                .code(ErrorConstants.QUARTZ_JOB_EXECUTION_ERROR)
                .build();
            //@formatter:on
        }
    }

    protected ExternalDatasetService getExternalDatasetService(JobExecutionContext context) {
        return getApplicationContext(context).getBean(ExternalDatasetService.class);
    }

    protected NotificationService getNotificationService(JobExecutionContext context) {
        return getApplicationContext(context).getBean(NotificationService.class);
    }

    protected ExternalOperationService getExternalOperationService(JobExecutionContext context) {
        return getApplicationContext(context).getBean(ExternalOperationService.class);
    }

    protected FavoriteService getFavoriteService(JobExecutionContext context) {
        return getApplicationContext(context).getBean(FavoriteService.class);
    }
}
