package es.gobcan.istac.edatos.external.users;

import es.gobcan.istac.edatos.external.users.core.config.DefaultProfileUtil;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.ExternalUserRole;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Language;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FavoriteRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class})
public class EdatosExternalUsersMigrationApp {

    private static ExternalUserRepository externalUserRepository;
    private static FilterRepository filterRepository;
    private static FavoriteRepository favoriteRepository;
    private static NotificationService notificationService;

    private static final Map<Long, Long> oldNewIdMap = new HashMap<>();
    private static final Set<ExternalUserRole> usersRoles = Collections.singleton(ExternalUserRole.USER);

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(EdatosExternalUsersMigrationApp.class);
        DefaultProfileUtil.addDefaultProfile(app);

        ConfigurableApplicationContext context = app.run(args);
        externalUserRepository = context.getBean(ExternalUserRepository.class);
        filterRepository = context.getBean(FilterRepository.class);
        favoriteRepository = context.getBean(FavoriteRepository.class);
        notificationService = context.getBean(NotificationService.class);

        String environment = args.length > 3 ? args[3] : "PRE";
        createUsers(args[0]);
        createFavorites(args[1]);
        createFilters(args[2], environment);

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
                    externalUser.setEmail(values[headerIndexMap.get("USU_EMAIL")]);
                    externalUser.setLanguage(Language.findByLocaleCode(values[headerIndexMap.get("USU_LANG")]));
                    externalUser.setOrganism(values[headerIndexMap.get("USU_ORGANISMO")]);
                    externalUser.setPhoneNumber(values[headerIndexMap.get("USU_TELEFONO")]);
                    externalUser.setEmailNotificationsEnabled(true);
                    externalUser.setPassword("MIGRATED_USER");
                    externalUser.setEnabled(false);
                    externalUser.setRegistrationToken(UUID.randomUUID().toString());
                    //externalUser.setGender(); // TODO fill with the values of current codelist, maybe it depends on the environment
                    externalUser.setRoles(usersRoles);
                    externalUser.setCreatedDate(parseInstant(values[headerIndexMap.get("USU_FECHACREACION")])); // TODO check if this value is overridden by spring

                    String[] nameParts = values[headerIndexMap.get("USU_NOMBRECOMPLETO")].split(" +");
                    if (nameParts.length == 2) {
                        externalUser.setName(nameParts[0]);
                        externalUser.setSurname1(nameParts[1]);
                    } else if (nameParts.length > 2) {
                        externalUser.setName(Arrays.asList(nameParts).subList(0, nameParts.length - 2).stream().collect(Collectors.joining(" ")));
                        externalUser.setSurname1(nameParts[nameParts.length - 2]);
                        externalUser.setSurname2(nameParts[nameParts.length - 1]);
                    }

                    ExternalUserEntity createdExternalUser = externalUserRepository.saveAndFlush(externalUser);
                    oldNewIdMap.put(Long.valueOf(values[headerIndexMap.get("USU_ID")]), createdExternalUser.getId());
                    sendEmailToCompleteRegistration(createdExternalUser);
                }
            }
        }
    }


    private static void createFavorites(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = csvReader.readNext()) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else {
                    FavoriteEntity favorite = new FavoriteEntity();
                    favorite.setExternalUser(externalUserRepository.findOne(oldNewIdMap.get(Long.valueOf(values[headerIndexMap.get("USU_ID")]))));
                    //favorite.setCategory(); // TODO
                    //favorite.setExternalOperation(); // TODO

                    favoriteRepository.saveAndFlush(favorite);
                }
            }
        }
    }

    private static void createFilters(String filename, String environment) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = csvReader.readNext()) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else {
                    FilterEntity filter = new FilterEntity();
                    filter.setName(values[headerIndexMap.get("URL_NOMBRE")]);
                    filter.setExternalUser(externalUserRepository.findOneByEmail(values[headerIndexMap.get("URL_USUARIO")]).orElseThrow(NullPointerException::new));
                    filter.setPermalink(buildPermalink(environment, values[headerIndexMap.get("URL_ID")]));
                    filter.setPermalinkType(PermalinkType.EXTERNAL);
                    //filter.setExternalOperation(); // TODO
                    filter.setCreatedDate(parseInstant(values[headerIndexMap.get("URL_FECHACREACION")]));

                    filterRepository.saveAndFlush(filter);
                }
            }
        }
    }

    private static Instant parseInstant(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        LocalDateTime localDateTime = LocalDateTime.parse(date, dateTimeFormatter);
        ZoneId zoneId = ZoneId.of("Europe/Paris"); // TODO check the hour of this instant
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }

    private static void sendEmailToCompleteRegistration(ExternalUserEntity externalUser) {
        notificationService.createCompleteRegistrationExternalUserAccountNotification(externalUser);
    }

    private static String buildPermalink(String environment, String id) {
        String preUrl = "https://proves.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=";
        String proUrl = "https://intranet.caib.es/ibestat-jaxi/generarUrlSocial.do?cod=";
        return ("PRO".equalsIgnoreCase(environment) ? proUrl : preUrl) + id;
    }
}
