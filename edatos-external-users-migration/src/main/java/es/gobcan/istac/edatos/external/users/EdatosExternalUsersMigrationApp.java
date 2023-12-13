package es.gobcan.istac.edatos.external.users;

import es.gobcan.istac.edatos.external.users.core.config.DefaultProfileUtil;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.ExternalUserRole;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Language;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FavoriteRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import liquibase.util.csv.CSVReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class})
public class EdatosExternalUsersMigrationApp {

    private static ExternalUserRepository externalUserRepository;
    private static FilterRepository filterRepository;
    private static FavoriteRepository favoriteRepository;
    private static NotificationService notificationService;
    private static ExternalOperationService externalOperationService;

    private static final Map<String, String> genders = new HashMap<>();
    private static final Map<Long, Long> oldNewIdMap = new HashMap<>();
    private static final Set<ExternalUserRole> usersRoles = Collections.singleton(ExternalUserRole.USER);

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(EdatosExternalUsersMigrationApp.class);
        DefaultProfileUtil.addDefaultProfile(app);

        genders.put("M", "1");
        genders.put("F", "2");
        genders.put("_U", "9");

        ConfigurableApplicationContext context = app.run(args);
        externalUserRepository = context.getBean(ExternalUserRepository.class);
        filterRepository = context.getBean(FilterRepository.class);
        favoriteRepository = context.getBean(FavoriteRepository.class);
        notificationService = context.getBean(NotificationService.class);
        externalOperationService = context.getBean(ExternalOperationService.class);

        createUsers(args[0]);
        createFavorites(args[1]);
        createFilters(args[2]);

        context.close();
    }

    private static void createUsers(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = csvReader.readNext()) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else {
                    ExternalUserEntity externalUser = new ExternalUserEntity();
                    externalUser.setEmail(values[headerIndexMap.get("email")]);
                    externalUser.setLanguage(Language.findByLocaleCode(values[headerIndexMap.get("language")]));
                    externalUser.setOrganism(values[headerIndexMap.get("organism")]);
                    externalUser.setPhoneNumber(values[headerIndexMap.get("phoneNumber")]);
                    externalUser.setEmailNotificationsEnabled(true);
                    externalUser.setPassword("MIGRATED_USER");
                    externalUser.setEnabled(false);
                    externalUser.setRegistrationToken(UUID.randomUUID().toString());
                    externalUser.setGender(genders.get(values[headerIndexMap.get("gender")]));
                    externalUser.setRoles(usersRoles);
                    externalUser.setName(values[headerIndexMap.get("name")]);
                    externalUser.setSurname1(values[headerIndexMap.get("surname1")]);
                    externalUser.setSurname2(values[headerIndexMap.get("surname2")]);

                    ExternalUserEntity createdExternalUser = externalUserRepository.saveAndFlush(externalUser);
                    createdExternalUser.setCreatedDate(parseDate(values[headerIndexMap.get("creationDate")]));
                    createdExternalUser = externalUserRepository.saveAndFlush(externalUser);
                    oldNewIdMap.put(Long.valueOf(values[headerIndexMap.get("id")]), createdExternalUser.getId());
                    sendEmailToCompleteRegistration(createdExternalUser);
                }
            }
        }
    }

    private static void createFavorites(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ',')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = csvReader.readNext()) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else {
                    ExternalOperationEntity externalOperation = externalOperationService.findByCode(values[headerIndexMap.get("externalOperation")]);
                    if (externalOperation.isEnabled()) {
                        FavoriteEntity favorite = new FavoriteEntity();
                        favorite.setExternalUser(externalUserRepository.findOne(oldNewIdMap.get(Long.valueOf(values[headerIndexMap.get("externalUser")]))));
                        favorite.setExternalOperation(externalOperation);

                        favoriteRepository.saveAndFlush(favorite);
                    }
                }
            }
        }
    }

    private static void createFilters(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ',')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = csvReader.readNext()) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else {
                    FilterEntity filter = new FilterEntity();
                    filter.setName(values[headerIndexMap.get("name")]);
                    filter.setExternalUser(externalUserRepository.findOne(oldNewIdMap.get(Long.valueOf(values[headerIndexMap.get("externalUser")]))));
                    filter.setPermalink(values[headerIndexMap.get("permalink")]);
                    filter.setPermalinkType(PermalinkType.EXTERNAL);
                    filter.setExternalOperation(externalOperationService.findByCode(values[headerIndexMap.get("externalOperation")]));
                    filter.setVisitCount(Long.parseLong(values[headerIndexMap.get("countVisits")]));
                    filter.setLastAccessDate(parseDateAndTime(values[headerIndexMap.get("lastAccessDate")]));

                    filter = filterRepository.saveAndFlush(filter);
                    filter.setCreatedDate(parseDateAndTime(values[headerIndexMap.get("creationDate")]));
                    filterRepository.saveAndFlush(filter);
                }
            }
        }
    }

    private static Instant parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/uu"))
            .atTime(12, 0)
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();
    }

    private static Instant parseDateAndTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();
    }

    private static void sendEmailToCompleteRegistration(ExternalUserEntity externalUser) {
        notificationService.createCompleteRegistrationExternalUserAccountNotification(externalUser);
    }
}
