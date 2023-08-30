package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.service.DataProtectionPolicyService;
import es.gobcan.istac.edatos.external.users.core.service.EDatosApisLocator;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.siemac.edatos.core.common.exception.CommonServiceExceptionType;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.siemac.metamac.rest.notices.v1_0.domain.Message;
import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;
import org.siemac.metamac.rest.notices.v1_0.domain.enume.MetamacApplicationsEnum;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.MessageBuilder;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.NoticeBuilder;
import org.siemac.metamac.rest.notices.v1_0.domain.utils.ReceiverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.CONFIRM_SIGNUP_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.EMAIL_IN_USE_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.REGISTER_ACCOUNT_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.REPEATED_SIGNUP_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.RESET_PASSWORD_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.SUBSCRIPTION_JOB_NOTICE_FAILED;
import static es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType.UNSUBSCRIBE_ACCOUNT_NOTICE_FAILED;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static String RESET_PASSWORD_URL = "/#/reset-password/change-password?key=";
    private static String CONFIRM_REGISTRATION_URL = "/#/login?registration-key=";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MetadataConfigurationService metadataConfigurationService;

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

        String[] args = argsByExternalUser(externalUserEntity);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), args, REGISTER_ACCOUNT_NOTICE_FAILED);
    }

    @Override
    public void createDeleteExternalUserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.unsubscribe_account.title";
        String messageCode = "notice.message.unsubscribe_account.text";
        String[] args = argsByExternalUser(externalUserEntity);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), args, UNSUBSCRIBE_ACCOUNT_NOTICE_FAILED);
    }

    @Override
    public void createResetPasswordExternaluserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.reset_password.title";
        String messageCode = "notice.message.reset_password.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + RESET_PASSWORD_URL + externalUserEntity.getResetKey();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), argsList.toArray(new String[argsList.size()]), RESET_PASSWORD_NOTICE_FAILED);
    }

    @Override
    public void createRegisterExternalUserAccountNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.confirm_signup.title";
        String messageCode = "notice.message.confirm_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONFIRM_REGISTRATION_URL + externalUserEntity.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), argsList.toArray(new String[argsList.size()]), CONFIRM_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createExternalUserAlreadyRegisteredNotification(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.email_in_use.title";
        String messageCode = "notice.message.email_in_use.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + RESET_PASSWORD_URL + externalUserEntity.getResetKey();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), argsList.toArray(new String[argsList.size()]), EMAIL_IN_USE_NOTICE_FAILED);
    }

    @Override
    public void createExternalUserRegistrationAlreadyRequested(ExternalUserEntity externalUserEntity) {
        String codeSubject = "notice.subject.repeated_signup.title";
        String messageCode = "notice.message.repeated_signup.text";
        String baseUrl = metadataConfigurationService.retrieveBaseUrl() + CONFIRM_REGISTRATION_URL + externalUserEntity.getRegistrationToken();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
        argsList.add(baseUrl);

        createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), argsList.toArray(new String[argsList.size()]), REPEATED_SIGNUP_NOTICE_FAILED);
    }

    @Override
    public void createNoticeOfSusbcritionsJob(List<FavoriteEntity> listFavorites) {
        String codeSubject = "notice.subject.subscrition_job.title";
        String messageCode = "notice.message.subscrition_job.text";

        for (FavoriteEntity favorite : listFavorites) {
            ExternalUserEntity externalUserEntity = favorite.getExternalUser();
            ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(argsByExternalUser(externalUserEntity)));
            argsList.add(favorite.getExternalOperation().getName().getLocalisedLabel(getLocale(), getDefaultLocale()));

            if (externalUserEntity.isEmailNotificationsEnabled() && favorite.getExternalOperation().isNotificationsEnabled()) {
                createNotificationWithReceiver(codeSubject, messageCode, externalUserEntity.getEmail(), argsList.toArray(new String[argsList.size()]), SUBSCRIPTION_JOB_NOTICE_FAILED);
            }
        }
    }

    private String[] argsByExternalUser(ExternalUserEntity externalUserEntity) {
        return new String[]{externalUserEntity.getName(), externalUserEntity.getSurname1(), externalUserEntity.getEmail()};
    }

    private void createNotificationWithReceiver(String codeSubject, String messageCode, String receiver, String[] args, CommonServiceExceptionType serviceExceptionTypeIfError) {
        Response createNoticeResponse;
        try {
            Notice notice = createNotice(codeSubject, messageCode, receiver, args);

            createNoticeResponse = eDatosApisLocator.getNoticesRestInternalFacadeV10().createNotice(notice);
        } catch (Exception e) {
            log.debug("Error al enviar notificacion : {}", e);
            throw new EDatosException(e, serviceExceptionTypeIfError);
        }
        if (createNoticeResponse != null && createNoticeResponse.getStatusInfo().getStatusCode() >= 400) {
            throw new EDatosException(serviceExceptionTypeIfError);
        }
    }

    private Notice createNotice(String codeSubject, String messageCode, String receiver, String[] args) {
        String subject = generateTextI18n(codeSubject, args);
        Message message = createMessage(messageCode, args);

        // @formatter:off
        return NoticeBuilder.notification()
            .withMessages(message, MessageBuilder.message().withText(getLopd()).build())
            .withReceivers(receiver)
            .withSendingApplication(MetamacApplicationsEnum.GESTOR_USUARIOS_EXTERNOS.getName())
            .withSubject(subject)
            .build();
        // @formatter:on
    }

    private Message createMessage(String messageCode, String[] args) {
        String localisedMessage = generateTextI18n(messageCode, args);

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
        for(String email : allReceiversEmails) {
            receivers.getReceivers().add(ReceiverBuilder.receiver().withUsername(email).build());
        }
        receivers.setTotal(new BigInteger(String.valueOf(receivers.getReceivers().size())));
        return receivers;
    }

    @Override
    public void createNotificationWithReceivers(String message, String subject, Receivers receivers) {
        try {
            // @formatter:off
            Notice notice = NoticeBuilder.notification()
                .withMessages(MessageBuilder.message().withText(message).build(), MessageBuilder.message().withText(getLopd()).build())
                .withReceivers(receivers)
                .withSendingApplication(MetamacApplicationsEnum.GESTOR_USUARIOS_EXTERNOS.getName())
                .withSubject(subject).build();
            // @formatter:on

            eDatosApisLocator.getNoticesRestInternalFacadeV10().createNotice(notice);
        } catch (Exception e) {
            log.debug("Error in sending notification : {}", e);
        }
    }

    private String generateTextI18n(String text, String[] args) {
        return messageSource.getMessage(text, args, LocaleContext.getLocale());
    }

    private String getOrganism() {
        return metadataConfigurationService.retrieveOrganisation();
    }

    private String getLopd() {
        return dataProtectionPolicyService.find().getValue().getLocalisedLabel(getLocale(), getDefaultLocale());
    }

    private String getLocale() {
        return LocaleContext.getLocale() != null ? LocaleContext.getLocale().toString() : null;
    }

    private String getDefaultLocale() {
        return LocaleContext.getDefaultLocale().toString();
    }
}
