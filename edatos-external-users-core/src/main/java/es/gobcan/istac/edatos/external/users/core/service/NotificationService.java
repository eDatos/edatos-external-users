package es.gobcan.istac.edatos.external.users.core.service;

import java.util.HashMap;
import java.util.List;

import org.siemac.edatos.core.common.dto.InternationalStringDto;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;

public interface NotificationService {

    void createNewExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createDeleteExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createResetPasswordExternaluserAccountNotification(ExternalUserEntity externalUserEntity);

    void createRegisterExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createExternalUserAlreadyRegisteredNotification(ExternalUserEntity externalUserEntity);
    void createExternalUserRegistrationAlreadyRequested(ExternalUserEntity externalUserEntity);

    void createNoticeOfSusbcritionsJob(List<FavoriteEntity> listFavorites);

    void createNotificationWithReceivers(InternationalStringDto message, InternationalStringDto subject, HashMap<String, Receivers> receivers);
    HashMap<String, Receivers> createReceiversGroupedByLanguage(List<String> emails, List<Long> categories, List<Long> externalOperations);
    void createContinueUserMigrationNotification(ExternalUserEntity user);
    void createResetPasswordMigratedUserNotification(ExternalUserEntity user);
    void createMigratedExternalUserPendingRegistrationNotification(ExternalUserEntity user);

}
