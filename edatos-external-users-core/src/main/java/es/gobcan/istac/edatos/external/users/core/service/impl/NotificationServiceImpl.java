package es.gobcan.istac.edatos.external.users.core.service.impl;

import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.CONFIRM_SIGNUP_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.EMAIL_IN_USE_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.MIGRATED_USER_SIGNUP_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.REGISTER_ACCOUNT_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.REPEATED_SIGNUP_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.RESET_PASSWORD_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.SUBSCRIPTION_JOB_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.UNSUBSCRIBE_ACCOUNT_NOTICE_FAILED;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import org.siemac.edatos.core.common.dto.InternationalStringDto;
import org.siemac.edatos.core.common.exception.CommonServiceExceptionType;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.siemac.metamac.rest.notices.v1_0.domain.Message;
import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.siemac.metamac.rest.notices.v1_0.domain.Notices;
import org.siemac.metamac.rest.notices.v1_0.domain.Receiver;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;
import org.siemac.metamac.rest.notices.v1_0.domain.enume.MetamacApplicationsEnum;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.MessageBuilder;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.NoticeBuilder;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.ReceiverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import es.gobcan.istac.edatos.external.users.core.service.DataProtectionPolicyService;
import es.gobcan.istac.edatos.external.users.core.service.EDatosApisLocator;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static String RESET_PASSWORD_URL = "/#/reset-password/change-password?key=";
    private static String CONFIRM_REGISTRATION_URL = "/#/login?registration-key=";
    private static String CONTINUE_SIGNUP_MIGRATED_USER_URL = "/#/signup?registration-token=";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MetadataConfigurationService metadataConfigurationService;

    @Autowired
    private MetadataProperties metadataProperties;

    @Autowired
    private EDatosApisLocator eDatosApisLocator;

    @Autowired
    private DataProtectionPolicyService dataProtectionPolicyService;

    @Autowired
    private ExternalUserService externalUserService;

    @Override
    public void createNewExternalUserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.register_account.title";
        String messageCode = "notice.message.register_account.text";
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(metadataProperties.getExternalAppUrl());

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, argsList.toArray(new String[0]), REGISTER_ACCOUNT_NOTICE_FAILED);
    }

    @Override
    public void createDeleteExternalUserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.unsubscribe_account.title";
        String messageCode = "notice.message.unsubscribe_account.text";
        String[] args = argsByExternalUser(externalUserEntity);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, args, UNSUBSCRIBE_ACCOUNT_NOTICE_FAILED);
    }

    @Override
    public void createResetPasswordExternaluserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.reset_password.title";
        String messageCode = "notice.message.reset_password.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + RESET_PASSWORD_URL + externalUserEntity.getResetKey();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, argsList.toArray(new String[argsList.size()]), RESET_PASSWORD_NOTICE_FAILED);
    }

    @Override
    public void createRegisterExternalUserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.confirm_signup.title";
        String messageCode = "notice.message.confirm_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONFIRM_REGISTRATION_URL + externalUserEntity.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, argsList.toArray(new String[argsList.size()]), CONFIRM_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createExternalUserAlreadyRegisteredNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.email_in_use.title";
        String messageCode = "notice.message.email_in_use.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + RESET_PASSWORD_URL + externalUserEntity.getResetKey();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, argsList.toArray(new String[argsList.size()]), EMAIL_IN_USE_NOTICE_FAILED);
    }

    @Override
    public void createExternalUserRegistrationAlreadyRequested(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.repeated_signup.title";
        String messageCode = "notice.message.repeated_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONFIRM_REGISTRATION_URL + externalUserEntity.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity, argsList.toArray(new String[argsList.size()]), REPEATED_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createNoticeOfSubscriptionsJob(Set<ExternalUserEntity> users, ExternalOperationEntity operation, List<ExternalDatasetEntity> datasets) {
        String codeSubject = "notice.subject.subscrition_job.title";
        String messageCode = "notice.message.subscrition_job.text";

        for (ExternalUserEntity user : users) {
            ArrayList<String> argsList = new ArrayList<>(Arrays.asList(argsByExternalUser(user)));
            argsList.add(operation.getName().getLocalisedLabel(user.getLanguage(), getDefaultLocale()));
            argsList.add(createListOfDatasetsForNotification(user, datasets));
            if (user.isEmailNotificationsEnabled() && operation.isNotificationsEnabled()) {
                createNotificationWithReceiver(codeSubject, messageCode, user, argsList.toArray(new String[0]), SUBSCRIPTION_JOB_NOTICE_FAILED);
            }
        }
    }

    private String createListOfDatasetsForNotification(ExternalUserEntity user, List<ExternalDatasetEntity> datasets) {
        List<String> datasetLabels = new ArrayList<>();
        for (ExternalDatasetEntity dataset : datasets) {
            String url = dataset.getVisualizerHtmlLink();
            String code = dataset.getCode();
            String label = dataset.getName().getLocalisedLabel(user.getLanguage(), getDefaultLocale());
            String line = MessageFormat.format("<li><a href=\"{0}\">{1} - {2}</a></li>", url, code, label);
            datasetLabels.add(line);
        }

        return String.join("\n", datasetLabels);
    }

    @Override
    public HashMap<String, Receivers> createReceiversGroupedByLanguage(List<String> emails, List<Long> categories, List<Long> externalOperations) {
        List<String> languages = metadataProperties.getInternationalizationLanguages().stream().map(language -> language.toLowerCase()).collect(Collectors.toList());
        HashMap<String, Receivers> receiversGroupByLanguage = new HashMap<>();

        List<ExternalUserEntity> usersByEmails = externalUserService.findAllByEmailIn(emails);
        List<ExternalUserEntity> usersSubscribedToCategoriesAndOperationsList = externalUserService.findByCategoriesAndExternalOperations(categories, externalOperations);

        for (String language : languages) {
            Set<String> allReceiversEmails = new HashSet<>();
            List<ExternalUserEntity> usersSubscribedToCategoriesAndOperationsFilterByLanguage = usersSubscribedToCategoriesAndOperationsList.stream()
                    .filter(user -> user.getLanguage().equals(language)).collect(Collectors.toList());
            allReceiversEmails.addAll(usersSubscribedToCategoriesAndOperationsFilterByLanguage.stream().map(ExternalUserEntity::getEmail).collect(Collectors.toList()));
            allReceiversEmails.addAll(usersByEmails.stream().filter(user -> user.getLanguage().equals(language)).map(ExternalUserEntity::getEmail).collect(Collectors.toList()));

            Receivers receivers = new Receivers();
            for (String email : allReceiversEmails) {
                receivers.getReceivers().add(ReceiverBuilder.receiver().withUsername(email).build());
            }

            receivers.setTotal(new BigInteger(String.valueOf(receivers.getReceivers().size())));
            receiversGroupByLanguage.put(language, receivers);
        }

        return receiversGroupByLanguage;
    }

    @Override
    public List<String> createNotificationWithReceivers(InternationalStringDto message, InternationalStringDto subject, HashMap<String, Receivers> receiversByLanguage) {
        List<String> failedNoticeReceivers = new ArrayList<>();
        for (String language : receiversByLanguage.keySet()) {
            Receivers receivers = receiversByLanguage.get(language);
            if (receivers.getTotal() != null && receivers.getTotal().intValue() > 0) {
                // @formatter:off
                    Notice notice = NoticeBuilder.notification()
                        .withMessages(MessageBuilder.message().withText(message.getLocalisedLabel(language)).build(), MessageBuilder.message().withText(getLopd(language)).build())
                        .withReceivers(receivers)
                        .withSendingApplication(MetamacApplicationsEnum.GESTOR_USUARIOS_EXTERNOS.getName())
                        .withSubject(subject.getLocalisedLabel(language)).build();
                    // @formatter:on

                Response createNoticeResponse = eDatosApisLocator.getNoticesRestInternalFacadeV10().createNotice(notice);
                if (createNoticeResponse != null && createNoticeResponse.getStatusInfo().getStatusCode() >= 400) {
                    failedNoticeReceivers.addAll(receivers.getReceivers().stream().map(Receiver::getUsername).collect(Collectors.toList()));
                    log.error("Error al enviar notificacion : {}", failedNoticeReceivers );
                }
            }
        }
        return failedNoticeReceivers;
    }


    private String[] argsByExternalUser(ExternalUserEntity externalUserEntity) {
        return new String[]{externalUserEntity.getName(), externalUserEntity.getSurname1(), externalUserEntity.getEmail()};
    }

    private void createNotificationWithReceiver(String codeSubject, String messageCode, ExternalUserEntity receiver, String[] args, CommonServiceExceptionType serviceExceptionTypeIfError) {
        Response createNoticeResponse;
        try {
            Notice notice = createNotice(codeSubject, messageCode, receiver.getEmail(), args, Locale.forLanguageTag(receiver.getLanguage()));

            createNoticeResponse = eDatosApisLocator.getNoticesRestInternalFacadeV10().createNotice(notice);
        } catch (Exception e) {
            log.debug("Error al enviar notificacion : {}", e);
            throw new EDatosException(e, serviceExceptionTypeIfError);
        }
        if (createNoticeResponse != null && createNoticeResponse.getStatusInfo().getStatusCode() >= 400) {
            throw new EDatosException(serviceExceptionTypeIfError);
        }
    }

    private Notice createNotice(String codeSubject, String messageCode, String receiver, String[] args, Locale locale) {
        String subject = generateTextI18n(codeSubject, args, locale);
        Message message = createMessage(messageCode, args, locale);

        // @formatter:off
        return NoticeBuilder.notification()
            .withMessages(message, MessageBuilder.message().withText(getLopd(locale.getLanguage())).build())
            .withReceivers(receiver)
            .withSendingApplication(MetamacApplicationsEnum.GESTOR_USUARIOS_EXTERNOS.getName())
            .withSubject(subject)
            .build();
        // @formatter:on
    }

    private Message createMessage(String messageCode, String[] args, Locale locale) {
        String localisedMessage = generateTextI18n(messageCode, args, locale);

        // @formatter:off
        return  MessageBuilder.message()
            .withText(localisedMessage)
            .build();
        // @formatter:on
    }

    @Override
    public Receivers createReceiversList(List<String> emails, List<Long> categories, List<Long> externalOperations) {
        List<ExternalUserEntity> usersSubscribedToCategoriesAndOperations = externalUserService.findByCategoriesAndExternalOperations(categories, externalOperations);

        Set<String> allReceiversEmails = new HashSet<>(emails);
        allReceiversEmails.addAll(usersSubscribedToCategoriesAndOperations.stream().map(ExternalUserEntity::getEmail).collect(Collectors.toList()));

        Receivers receivers = new Receivers();
        for (String email : allReceiversEmails) {
            receivers.getReceivers().add(ReceiverBuilder.receiver().withUsername(email).build());
        }
        receivers.setTotal(new BigInteger(String.valueOf(receivers.getReceivers().size())));
        return receivers;
    }

    @Override
    public void createContinueUserMigrationNotification(ExternalUserEntity user) {
        String codeSubject = "notice.subject.continue_migration_signup.title";
        String messageCode = "notice.subject.continue_migration_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONTINUE_SIGNUP_MIGRATED_USER_URL + user.getRegistrationToken();

        createNotificationWithReceiver(codeSubject, messageCode, user, new String[]{baseUrl}, MIGRATED_USER_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createResetPasswordMigratedUserNotification(ExternalUserEntity user) {
        String codeSubject = "notice.subject.migrated_user_reset_password.title";
        String messageCode = "notice.subject.migrated_user_reset_password.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONTINUE_SIGNUP_MIGRATED_USER_URL + user.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(argsByExternalUser(user)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, user, argsList.toArray(new String[0]), MIGRATED_USER_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createMigratedExternalUserPendingRegistrationNotification(ExternalUserEntity user) {
        String codeSubject = "notice.subject.migrated_user_repeated_signup.title";
        String messageCode = "notice.subject.migrated_user_repeated_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONTINUE_SIGNUP_MIGRATED_USER_URL + user.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(argsByExternalUser(user)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, user, argsList.toArray(new String[0]), MIGRATED_USER_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createNotificationWithReceivers(String message, String subject, Receivers receivers) {
        try {
            // @formatter:off
            Notice notice = NoticeBuilder.notification()
                .withMessages(MessageBuilder.message().withText(message).build(), MessageBuilder.message().withText(getLopd(null)).build())
                .withReceivers(receivers)
                .withSendingApplication(MetamacApplicationsEnum.GESTOR_USUARIOS_EXTERNOS.getName())
                .withSubject(subject).build();
            // @formatter:on

            eDatosApisLocator.getNoticesRestInternalFacadeV10().createNotice(notice);
        } catch (Exception e) {
            log.debug("Error in sending notification : {}", e);
        }
    }

    @Override
    public Page<Notice> findNoticesByReceiver(String receiver, Pageable pageable) {
        String offset = String.valueOf(pageable.getOffset());
        String limit = String.valueOf(pageable.getPageSize());
        String orderBy = "desc";

        Notices result = eDatosApisLocator.getNoticesRestInternalFacadeV10().findNoticesByReceiver(receiver, orderBy, offset, limit);

        return new PageImpl<Notice>(result.getNotices(), pageable, result.getTotal().intValue());
    }


    public Notice findNoticeByUrn(String urn) {
        return eDatosApisLocator.getNoticesRestInternalFacadeV10().retrieveNoticeByUrn(urn);
    }


    private String generateTextI18n(String text, String[] args, Locale locale) {
        return messageSource.getMessage(text, args, locale);
    }

    private String getOrganism() {
        return metadataConfigurationService.retrieveOrganisation();
    }

    private String getLopd(String locale) {
        return dataProtectionPolicyService.find().getValue().getLocalisedLabel(locale, getDefaultLocale());
    }

    private String getDefaultLocale() {
        return LocaleContext.getDefaultLocale().toString();
    }

    private Message initStyleClassMessageTemplate() {
        String message = "<div class=\"message-content-notice\">";
        return MessageBuilder.message().withText(message).build();
    }

    private Message finalStyleClassMessageTemplate() {
        String message = "</div>";
        return MessageBuilder.message().withText(message).build();
    }
}
