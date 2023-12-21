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
    private static ConfigurableApplicationContext context;

    private static final Map<String, ExternalUserEntity> oldIdNewUserMap = new HashMap<>();
    private static final Set<ExternalUserRole> usersRoles = Collections.singleton(ExternalUserRole.USER);

    private static boolean sendEmail = false;

    private static long migratedUsers = 0;
    private static long totalUsers = 0;
    private static long migratedFavorites = 0;
    private static long totalFavorites = 0;
    private static long migratedFilters = 0;
    private static long totalFilters = 0;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(EdatosExternalUsersMigrationApp.class);
        DefaultProfileUtil.addDefaultProfile(app);

        context = app.run(args);
        externalUserRepository = context.getBean(ExternalUserRepository.class);
        filterRepository = context.getBean(FilterRepository.class);
        favoriteRepository = context.getBean(FavoriteRepository.class);
        notificationService = context.getBean(NotificationService.class);
        externalOperationService = context.getBean(ExternalOperationService.class);

        sendEmail = args.length > 3 && args[3].equalsIgnoreCase("--email");

        log.info("\n\n--------------------------------- COMIENZO DE LA MIGRACIÓN ---------------------------------\n");
        createUsers(args[0]);
        createFavorites(args[1]);
        createFilters(args[2]);

        log.info("\n\n--------------------------------- RESUMEN DE LA MIGRACIÓN ---------------------------------\n");
        log.info("MIGRATED USERS: " + migratedUsers + "/" + totalUsers);
        log.info("MIGRATED FAVORITES: " + migratedFavorites + "/" + totalFavorites);
        log.info("MIGRATED FILTERS: " + migratedFilters + "/" + totalFilters);
        if (migratedUsers != totalUsers || migratedFavorites != totalFavorites || migratedFilters != totalFilters) {
            log.warn("No se han podido migrar todas las filas. Haz los cambios necesarios para arreglar las filas con errores y vuelve a intentarlo.");
        }

        log.info("\n\n--------------------------------- FIN DE LA MIGRACIÓN ---------------------------------\n");
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }

    private static void createUsers(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    log.error("Usuarios. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'.");
                    return;
                } else {
                    List<Exception> exceptions = new ArrayList<>();
                    try {
                        PlatformTransactionManager transactionManager = context.getBean(PlatformTransactionManager.class);
                        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                        String[] finalValues = values;
                        transactionTemplate.execute(status -> {
                            ExternalUserEntity externalUser = externalUserRepository.findOneByEmail(finalValues[headerIndexMap.get("email")]).orElse(new ExternalUserEntity());
                            if (externalUser.getId() != null) {
                                log.warn("El usuario con correo " + finalValues[headerIndexMap.get("email")] + " ya existe. Será ignorado.");
                                oldIdNewUserMap.put(finalValues[headerIndexMap.get("id")], externalUser);
                            } else {
                                externalUser.setEmail(finalValues[headerIndexMap.get("email")]);
                                if (StringUtils.isNotBlank(finalValues[headerIndexMap.get("language")])) {
                                    Language language = Language.findByLocaleCode(finalValues[headerIndexMap.get("language")]);
                                    if (language == null) {
                                        exceptions.add(new RuntimeException("No se ha podido migrar el lenguaje porque no es uno de los valores permitidos."));
                                    } else {
                                        externalUser.setLanguage(language);
                                    }
                                }
                                externalUser.setOrganism(finalValues[headerIndexMap.get("organism")]);
                                externalUser.setPhoneNumber(finalValues[headerIndexMap.get("phoneNumber")]);
                                externalUser.setEmailNotificationsEnabled(true);
                                externalUser.setPassword("MIGRATED_USER");
                                externalUser.setEnabled(false);
                                externalUser.setRegistrationToken(UUID.randomUUID().toString());
                                if (StringUtils.isNotBlank(finalValues[headerIndexMap.get("gender")])) {
                                    String gender = StringUtils.substringAfterLast(finalValues[headerIndexMap.get("gender")], ".");
                                    if (StringUtils.isBlank(gender)) {
                                        exceptions.add(new RuntimeException("No se ha podido migrar el género porque el formato de la urn no es correcto."));
                                    } else {
                                        externalUser.setGender(gender);
                                    }
                                }
                                externalUser.setRoles(usersRoles);
                                externalUser.setName(finalValues[headerIndexMap.get("name")]);
                                externalUser.setSurname1(finalValues[headerIndexMap.get("surname1")]);
                                externalUser.setSurname2(finalValues[headerIndexMap.get("surname2")]);

                                ExternalUserEntity createdExternalUser = externalUserRepository.saveAndFlush(externalUser);
                                Instant createdDate = parseDate(finalValues[headerIndexMap.get("creationDate")]);
                                if (createdDate != null) {
                                    createdExternalUser.setCreatedDate(createdDate);
                                }
                                if (!exceptions.isEmpty()) {
                                    status.setRollbackOnly();
                                } else {
                                    oldIdNewUserMap.put(finalValues[headerIndexMap.get("id")], createdExternalUser);
                                    if (sendEmail) {
                                        try {
                                            sendEmailToCompleteRegistration(createdExternalUser);
                                        } catch (Exception e) {
                                            exceptions.add(new RuntimeException("Ha habido un error mandando el correo al usuario " + createdExternalUser.getEmail() + ". Se ha impedido su migración para que no se quede sin acceso al correo."));
                                            status.setRollbackOnly();
                                        }
                                    }
                                }
                            }
                            return "";
                        });
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                    if (!exceptions.isEmpty()) {
                        StringBuilder errorLog = new StringBuilder("Usuario. Línea " + Arrays.toString(values) + ":\n");
                        for(Exception e: exceptions) {
                            errorLog.append("- ").append(e.getMessage()).append("\n");
                        }
                        log.error(errorLog.toString());
                    } else {
                        migratedUsers++;
                    }
                    totalUsers++;
                }
            }
        }
    }

    private static void createFavorites(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    log.error("Favoritos. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'.");
                    return;
                } else {
                    List<Exception> exceptions = new ArrayList<>();
                    try {
                        PlatformTransactionManager transactionManager = context.getBean(PlatformTransactionManager.class);
                        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                        String[] finalValues = values;
                        transactionTemplate.execute(status -> {
                            FavoriteEntity favorite = new FavoriteEntity();

                            ExternalUserEntity favoriteOwner = oldIdNewUserMap.getOrDefault(finalValues[headerIndexMap.get("externalUser")], null);
                            if (favoriteOwner == null) {
                                exceptions.add(new RuntimeException("No existe ningún usuario que se pueda atribuir al id proporcionado. Revise si ese usuario existe o si ha ocurrido un error durante la migración de dicho usuario."));
                            } else {
                                favorite.setExternalUser(favoriteOwner);
                            }

                            ExternalOperationEntity externalOperation = externalOperationService.findByCode(finalValues[headerIndexMap.get("externalOperation")]);
                            if (externalOperation == null) {
                                exceptions.add(new RuntimeException("No se puede tener crear un favorito sin una operación o tema asignado. Compruebe que ese campo tiene valor en el csv. Si es así, quizás la operación no se encuentra en e-Usuarios. Relance el envío por Kafka."));
                            } else if (!externalOperation.isEnabled()) {
                                exceptions.add(new RuntimeException("No se puede tener como favorita una operación desactivada."));
                            } else {
                                favorite.setExternalOperation(externalOperation);
                            }

                            if (externalOperation != null && favoriteOwner != null) {
                                if (favoriteRepository.findByExternalUserAndExternalOperation(favoriteOwner, externalOperation).orElse(null) == null) {
                                    favoriteRepository.saveAndFlush(favorite);
                                    if (!exceptions.isEmpty()) {
                                        status.setRollbackOnly();
                                    }
                                } else {
                                    log.warn("El favorito del usuario " + favoriteOwner.getEmail() + " y operación " + externalOperation.getCode() + " ya existe. Será ignorado.");
                                    exceptions.removeIf(exception -> true);
                                }
                            }
                            return "";
                        });
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                    if (!exceptions.isEmpty()) {
                        StringBuilder errorLog = new StringBuilder("Favorito. Línea " + Arrays.toString(values) + ":\n");
                        for(Exception e: exceptions) {
                            errorLog.append("- ").append(e.getMessage()).append("\n");
                        }
                        log.error(errorLog.toString());
                    } else {
                        migratedFavorites++;
                    }
                    totalFavorites++;
                }
            }
        }
    }



    private static void createFilters(String filename) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename), ';')) {
            String[] values;
            Map<String, Integer> headerIndexMap = new HashMap<>();
            while ((values = getNextRowValues(csvReader)) != null) {
                if (headerIndexMap.isEmpty()) {
                    for (int i = 0; i < values.length; i++) {
                        headerIndexMap.put(values[i], i);
                    }
                } else if (headerIndexMap.size() == 1) {
                    log.error("Filtros. No ha sido posible separar las diferentes columnas. Por favor, compruebe que hay más de una columna y que el csv se ha separado con el símbolo ';'.");
                } else {
                    List<Exception> exceptions = new ArrayList<>();

                    try {
                        PlatformTransactionManager transactionManager = context.getBean(PlatformTransactionManager.class);
                        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                        String[] finalValues = values;
                        transactionTemplate.execute(status -> {
                            FilterEntity filter = new FilterEntity();
                            filter.setPermalink(finalValues[headerIndexMap.get("permalink")]);

                            ExternalUserEntity favoriteOwner = oldIdNewUserMap.getOrDefault(finalValues[headerIndexMap.get("externalUser")], null);
                            if (favoriteOwner == null) {
                                exceptions.add(new RuntimeException("No existe ningún usuario que se pueda atribuir al id proporcionado. Revise si ese usuario existe o si ha ocurrido un error durante la migración de dicho usuario."));
                            } else {
                                filter.setExternalUser(favoriteOwner);
                            }
                            if (filterRepository.findByExternalUserAndPermalink(favoriteOwner, filter.getPermalink()).orElse(null) != null) {
                                log.warn("El filtro del usuario " + favoriteOwner.getEmail() + " y permalink " + filter.getPermalink() + " ya existe. Será ignorado.");
                                exceptions.removeIf(exception -> true);
                            } else {
                                filter.setName(finalValues[headerIndexMap.get("name")]);
                                filter.setPermalinkType(PermalinkType.EXTERNAL);
                                filter.setExternalOperation(externalOperationService.findByCode(finalValues[headerIndexMap.get("externalOperation")]));

                                ExternalOperationEntity externalOperation = externalOperationService.findByCode(finalValues[headerIndexMap.get("externalOperation")]);
                                if (externalOperation == null && StringUtils.isNotBlank(finalValues[headerIndexMap.get("externalOperation")])) {
                                    exceptions.add(new RuntimeException(
                                        "La operación externa referenciada en el filtro no existe en eUsuarios. Quizás la operación no se encuentra en e-Usuarios. Relance el envío por Kafka."));
                                } else {
                                    filter.setExternalOperation(externalOperation);
                                }

                                String countVisitsColumn = finalValues[headerIndexMap.get("countVisits")];
                                if (StringUtils.isNotBlank(countVisitsColumn)) {
                                    filter.setVisitCount(Long.parseLong(countVisitsColumn));
                                }

                                Instant lastAccessDate = parseDateAndTime(finalValues[headerIndexMap.get("lastAccessDate")]);
                                if (lastAccessDate != null) {
                                    filter.setLastAccessDate(lastAccessDate);
                                }

                                filter = filterRepository.saveAndFlush(filter);
                                Instant createdDate = parseDateAndTime(finalValues[headerIndexMap.get("creationDate")]);
                                if (createdDate != null) {
                                    filter.setCreatedDate(createdDate);
                                }

                                if (!exceptions.isEmpty()) {
                                    status.setRollbackOnly();
                                }
                            }
                            return "";
                        });
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                    if (!exceptions.isEmpty()) {
                        StringBuilder errorLog = new StringBuilder("Filtro. Línea " + Arrays.toString(values) + ":\n");
                        for(Exception e: exceptions) {
                            errorLog.append("- ").append(e.getMessage()).append("\n");
                        }
                        log.error(errorLog.toString());
                    } else {
                        migratedFilters++;
                    }
                    totalFilters++;
                }
            }
        }
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
        notificationService.createContinueUserMigrationNotification(externalUser);
    }
}
