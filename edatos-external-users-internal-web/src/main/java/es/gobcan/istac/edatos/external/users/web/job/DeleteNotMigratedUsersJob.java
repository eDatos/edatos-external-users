package es.gobcan.istac.edatos.external.users.web.job;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.web.config.QuartzConfiguration;
import es.gobcan.istac.edatos.external.users.web.config.QuartzConstants;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Component
@ConditionalOnProperty(name = DeleteNotMigratedUsersJob.APPLICATION_JOBS_MIGRATION_DATE)
public class DeleteNotMigratedUsersJob extends AbstractConsumerQuartzJob {

    public static final String APPLICATION_JOBS_MIGRATION_DATE = "application.jobs.migration.date";

    private final Logger log = LoggerFactory.getLogger(DeleteNotMigratedUsersJob.class);

    @Bean
    public JobDetailFactoryBean jobDeleteNotMigratedUsers() {
        return QuartzConfiguration.createJobDetail(DeleteNotMigratedUsersJob.class, QuartzConstants.DELETE_NOT_MIGRATED_USERS_JOB, QuartzConstants.DELETE_NOT_MIGRATED_USERS_DESCRIPTION);
    }

    @Bean
    public SimpleTriggerFactoryBean triggerMemberClassStats(@Qualifier("jobDeleteNotMigratedUsers") JobDetail jobDetail, Environment env) {
        String date = env.getProperty(APPLICATION_JOBS_MIGRATION_DATE);
        return QuartzConfiguration.createDateTrigger(jobDetail, Date.from(Instant.parse(date)), QuartzConstants.DELETE_NOT_MIGRATED_USERS_TRIGGER);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("--- INIT --- Job DeleteNotMigratedUsersJob running");

        PlatformTransactionManager platformTransactionManager = getPlatformTransactionManager(context);
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);

        transactionTemplate.execute((TransactionCallback<Void>) status -> {
            log.info("Job " + DeleteNotMigratedUsersJob.class.getName() + " running into transaction");
            try {
                List<ExternalUserEntity> externalUsersDeleted = getExternalUserService(context).deleteUsersThatDidntCompleteMigration();
                log.info("Job " + DeleteNotMigratedUsersJob.class.getName() + " deleted " + externalUsersDeleted.size() + " users");
            } catch (Exception e) {
                log.error(DeleteNotMigratedUsersJob.class.getName() + " error: ", e);
                throw new EDatosException();
            }
            return null;
        });

        log.info("--- FINISHED --- Job " + DeleteNotMigratedUsersJob.class.getName() + " finished.");
    }

}
