package es.gobcan.istac.edatos.external.users.web.config;

import org.apache.commons.lang3.ArrayUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.*;

@Configuration
public class QuartzConfiguration {

    private static final Logger log = LoggerFactory.getLogger(QuartzConfiguration.class);

    @Autowired
    private DataSource dataSource;

    // source https://dzone.com/articles/adding-quartz-to-spring-boot
    @Bean
    public SchedulerFactoryBean quartz(Trigger... triggers) throws IOException {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setOverwriteExistingJobs(true);
        bean.setDataSource(dataSource);
        bean.setApplicationContextSchedulerContextKey(QuartzConstants.APPLICATION_CONTEXT_KEY);
        bean.setQuartzProperties(quartzProperties());
        bean.setAutoStartup(true);
        bean.setWaitForJobsToCompleteOnShutdown(true);

        if (ArrayUtils.isNotEmpty(triggers)) {
            bean.setTriggers(triggers);
        }

        return bean;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/config/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs, String triggerName) {
        log.debug("createTrigger(jobDetail={}, pollFrequencyMs={}, triggerName={})", jobDetail.toString(), pollFrequencyMs, triggerName);

        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setName(triggerName);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);

        return factoryBean;
    }

    static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression, String triggerName) {
        log.debug("createCronTrigger(jobDetail={}, cronExpression={}, triggerName={})", jobDetail.toString(), cronExpression, triggerName);

        // To fix an issue with time-based cron jobs
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setStartTime(calendar.getTime());
        factoryBean.setStartDelay(0L);
        factoryBean.setName(triggerName);
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);

        return factoryBean;
    }

    static JobDetailFactoryBean createJobDetail(Class jobClass, String jobName, String jobDescription) {
        log.debug("createJobDetail(jobClass={}, jobName={})", jobClass.getName(), jobName);

        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setName(jobName);
        factoryBean.setDescription(jobDescription);
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(true);

        return factoryBean;
    }
}
