package es.gobcan.istac.edatos.external.users.core.config;

import javax.sql.DataSource;

import org.siemac.edatos.core.common.datasource.EdatosHikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.arte.libs.grammar.repository.support.ArteJpaRepositoryFactoryBean;
import com.zaxxer.hikari.HikariDataSource;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import io.github.jhipster.config.JHipsterConstants;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableJpaRepositories(basePackages = "es.gobcan.istac.edatos.external.users.core.repository", repositoryFactoryBeanClass = ArteJpaRepositoryFactoryBean.class)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private final Logger      log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private final Environment env;

    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean(destroyMethod = "close")
    @Primary
    public DataSource dataSource(MetadataConfigurationService metadataConfigurationService) {
        // @formatter:off
        HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder.create().
                driverClassName(metadataConfigurationService.retrieveProperty(DataConfigurationConstants.DB_DRIVER_NAME)).
                url(metadataConfigurationService.retrieveProperty(DataConfigurationConstants.DB_URL)).
                username(metadataConfigurationService.findProperty(DataConfigurationConstants.DB_USERNAME)).
                password(metadataConfigurationService.findProperty(DataConfigurationConstants.DB_PASSWORD)).
                type(EdatosHikariDataSource.class).
                build();
        // @formatter:on

        hikariDataSource.setMaximumPoolSize(5);
        return hikariDataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        if (env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE)) {
            liquibase.setShouldRun(false);
        } else {
            liquibase.setShouldRun(liquibaseProperties.isEnabled());
            log.debug("Configurando Liquibase");
        }
        return liquibase;
    }
}
