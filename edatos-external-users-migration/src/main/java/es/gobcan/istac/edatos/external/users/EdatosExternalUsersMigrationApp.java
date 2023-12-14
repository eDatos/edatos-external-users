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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ComponentScan
@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@EnableConfigurationProperties({LiquibaseProperties.class})
public class EdatosExternalUsersMigrationApp {

    private static final Logger log = LoggerFactory.getLogger(EdatosExternalUsersMigrationApp.class);

    private static ExternalUserRepository externalUserRepository;
    private static FilterRepository filterRepository;
    private static FavoriteRepository favoriteRepository;
    private static NotificationService notificationService;
    private static ExternalOperationService externalOperationService;

    private static final Map<String, ExternalUserEntity> oldIdNewUserMap = new HashMap<>();
    private static final Set<ExternalUserRole> usersRoles = Collections.singleton(ExternalUserRole.USER);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EdatosExternalUsersMigrationApp.class);
        DefaultProfileUtil.addDefaultProfile(app);

        ConfigurableApplicationContext context = app.run(args);
        externalUserRepository = context.getBean(ExternalUserRepository.class);
        filterRepository = context.getBean(FilterRepository.class);
        favoriteRepository = context.getBean(FavoriteRepository.class);
        notificationService = context.getBean(NotificationService.class);
        externalOperationService = context.getBean(ExternalOperationService.class);

        PlatformTransactionManager transactionManager = context.getBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        List<Exception> exceptions = transactionTemplate.execute(status -> {
            List<Exception> migrationExceptions;
            try {
                migrationExceptions = createUsers(args[0]);
            } catch (IOException e) {
                return Collections.singletonList(e);
            }

            if (migrationExceptions.isEmpty()) {
                try {
                    migrationExceptions.addAll(createFavorites(args[1]));
                } catch (IOException e) {
                    return Collections.singletonList(e);
                }
                try {
                    migrationExceptions.addAll(createFilters(args[2]));
                } catch (IOException e) {
                    return Collections.singletonList(e);
                }
            }

            if (!migrationExceptions.isEmpty()) {
                status.setRollbackOnly();
            }
            return migrationExceptions;
        });
        if (exceptions.isEmpty()) {
            for (ExternalUserEntity user : oldIdNewUserMap.values()) {
                sendEmailToCompleteRegistration(user);
            }
        } else {
            for (Exception exception : exceptions) {
                log.error(exception.getMessage());
            }
        }

        SpringApplication.exit(context, () -> 0);
    }

    private static List<Exception> createUsers(String filename) throws IOException {
        List<Exception> exceptions = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    exceptions.add(new RuntimeException("Usuarios. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'."));
                    return exceptions;
                } else {
                    try {
                        ExternalUserEntity externalUser = new ExternalUserEntity();
                        externalUser.setEmail(values[headerIndexMap.get("email")]);
                        if (StringUtils.isNotBlank(values[headerIndexMap.get("language")])) {
                            Language language = Language.findByLocaleCode(values[headerIndexMap.get("language")]);
                            if (language == null) {
                                exceptions.add(new RuntimeException("Usuarios. Línea " + Arrays.toString(values) + ": No se ha podido migrar el lenguaje porque no es uno de los valores permitidos."));
                            } else {
                                externalUser.setLanguage(language);
                            }
                        }
                        externalUser.setOrganism(values[headerIndexMap.get("organism")]);
                        externalUser.setPhoneNumber(values[headerIndexMap.get("phoneNumber")]);
                        externalUser.setEmailNotificationsEnabled(true);
                        externalUser.setPassword("MIGRATED_USER");
                        externalUser.setEnabled(false);
                        externalUser.setRegistrationToken(UUID.randomUUID().toString());
                        if (StringUtils.isNotBlank(values[headerIndexMap.get("gender")])) {
                            String gender = StringUtils.substringAfterLast(values[headerIndexMap.get("gender")], ".");
                            if (StringUtils.isBlank(gender)) {
                                exceptions.add(new RuntimeException("Usuarios. Línea " + Arrays.toString(values) + ": No se ha podido migrar el género porque el formato de la urn no es correcto."));
                            } else {
                                externalUser.setGender(gender);
                            }
                        }
                        externalUser.setRoles(usersRoles);
                        externalUser.setName(values[headerIndexMap.get("name")]);
                        externalUser.setSurname1(values[headerIndexMap.get("surname1")]);
                        externalUser.setSurname2(values[headerIndexMap.get("surname2")]);

                        ExternalUserEntity createdExternalUser = externalUserRepository.saveAndFlush(externalUser);
                        Instant createdDate = parseDate(values[headerIndexMap.get("creationDate")]);
                        if (createdDate != null) {
                            createdExternalUser.setCreatedDate(createdDate);
                        }

                        oldIdNewUserMap.put(values[headerIndexMap.get("id")], createdExternalUser);
                    } catch (Exception e) {
                        exceptions.add(new RuntimeException("Usuarios. Línea " + Arrays.toString(values) + ": " + e.getMessage()));
                    }
                }
            }
        }
        return exceptions;
    }

    private static List<Exception> createFavorites(String filename) throws IOException {
        List<Exception> exceptions = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    exceptions.add(new RuntimeException("Favoritos. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'."));
                } else {
                    try {
                        FavoriteEntity favorite = new FavoriteEntity();

                        ExternalUserEntity favoriteOwner = oldIdNewUserMap.getOrDefault(values[headerIndexMap.get("externalUser")], null);
                        if (favoriteOwner == null) {
                            exceptions.add(new RuntimeException("Favoritos. Línea " + Arrays.toString(values) + ": No existe ningún usuario que se pueda atribuir al id proporcionado."));
                        } else {
                            favorite.setExternalUser(favoriteOwner);
                        }

                        ExternalOperationEntity externalOperation = externalOperationService.findByCode(values[headerIndexMap.get("externalOperation")]);
                        if (externalOperation == null) {
                            exceptions.add(new RuntimeException("Favoritos. Línea " + Arrays.toString(values) + ": No se puede tener crear un favorito sin una operación o tema asignado. Compruebe que ese campo tiene valor en el csv. Si es así, quizás la operación no se encuentra en e-Usuarios. Relance el envío por Kafka."));
                        } else if (!externalOperation.isEnabled()) {
                            exceptions.add(new RuntimeException("Favoritos. Línea " + Arrays.toString(values) + ": No se puede tener como favorita una operación desactivada."));
                        } else {
                            favorite.setExternalOperation(externalOperation);
                        }

                        favoriteRepository.saveAndFlush(favorite);
                    } catch (Exception e) {
                        exceptions.add(new RuntimeException("Favoritos. Línea " + Arrays.toString(values) + ": " + e.getMessage()));
                    }
                }
            }
        }
        return exceptions;
    }



    private static List<Exception> createFilters(String filename) throws IOException {
        List<Exception> exceptions = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    exceptions.add(new RuntimeException("Filtros. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'."));
                } else {
                    try {
                        FilterEntity filter = new FilterEntity();
                        filter.setName(values[headerIndexMap.get("name")]);

                        ExternalUserEntity favoriteOwner = oldIdNewUserMap.getOrDefault(values[headerIndexMap.get("externalUser")], null);
                        if (favoriteOwner == null) {
                            exceptions.add(new RuntimeException("Filtros. Línea " + Arrays.toString(values) + ": No existe ningún usuario que se pueda atribuir al id proporcionado."));
                        } else {
                            filter.setExternalUser(favoriteOwner);
                        }

                        filter.setPermalink(values[headerIndexMap.get("permalink")]);
                        filter.setPermalinkType(PermalinkType.EXTERNAL);
                        filter.setExternalOperation(externalOperationService.findByCode(values[headerIndexMap.get("externalOperation")]));

                        ExternalOperationEntity externalOperation = externalOperationService.findByCode(values[headerIndexMap.get("externalOperation")]);
                        if (externalOperation == null && StringUtils.isNotBlank(values[headerIndexMap.get("externalOperation")])) {
                            exceptions.add(new RuntimeException("Filtros. Línea " + Arrays.toString(values) + ": La operación externa referenciada en el filtro no existe en eUsuarios. Quizás la operación no se encuentra en e-Usuarios. Relance el envío por Kafka."));
                        } else {
                            filter.setExternalOperation(externalOperation);
                        }

                        String countVisitsColumn = values[headerIndexMap.get("countVisits")];
                        if (StringUtils.isNotBlank(countVisitsColumn)) {
                            filter.setVisitCount(Long.parseLong(countVisitsColumn));
                        }

                        Instant lastAccessDate = parseDateAndTime(values[headerIndexMap.get("lastAccessDate")]);
                        if (lastAccessDate != null) {
                            filter.setLastAccessDate(lastAccessDate);
                        }

                        filter = filterRepository.saveAndFlush(filter);
                        Instant createdDate = parseDateAndTime(values[headerIndexMap.get("creationDate")]);
                        if (createdDate != null) {
                            filter.setCreatedDate(createdDate);
                            filterRepository.saveAndFlush(filter);
                        }
                    } catch (Exception e) {
                        exceptions.add(new RuntimeException("Filtros. Línea " + Arrays.toString(values) + ": " + e.getMessage()));
                    }
                }
            }
        }
        return exceptions;
    }

    private static String[] getNextRowValues(CSVReader reader) throws IOException {
        String[] rowValues = reader.readNext();
        return rowValues == null ? null : Arrays.stream(rowValues).map(rowValue -> StringUtils.isNotBlank(rowValue) ? rowValue : null).toArray(String[]::new);
    }

    private static Instant parseDate(String date) {
        return StringUtils.isBlank(date) ? null : LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/uu"))
            .atTime(12, 0)
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();
    }

    private static Instant parseDateAndTime(String date) {
        return StringUtils.isBlank(date) ? null : LocalDateTime.parse(date, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"))
            .atZone(ZoneId.of("Europe/Paris"))
            .toInstant();
    }

    private static void sendEmailToCompleteRegistration(ExternalUserEntity externalUser) {
        notificationService.createCompleteRegistrationExternalUserAccountNotification(externalUser);
    }
}
