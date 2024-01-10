package es.gobcan.istac.edatos.external.users.core.service;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;

import java.util.List;

import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNewExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createDeleteExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createResetPasswordExternaluserAccountNotification(ExternalUserEntity externalUserEntity);

    void createRegisterExternalUserAccountNotification(ExternalUserEntity externalUserEntity);
    void createExternalUserAlreadyRegisteredNotification(ExternalUserEntity externalUserEntity);
    void createExternalUserRegistrationAlreadyRequested(ExternalUserEntity externalUserEntity);

    void createNoticeOfSusbcritionsJob(List<FavoriteEntity> listFavorites);

    void createNotificationWithReceivers(String message, String subject, Receivers receivers);
    Receivers createReceiversList(List<String> emails, List<Long> categories, List<Long> externalOperations);
    Page<Notice> findNoticesByReceiver(String receiver, Pageable pageable);

    void createContinueUserMigrationNotification(ExternalUserEntity user);
    void createResetPasswordMigratedUserNotification(ExternalUserEntity user);
    void createMigratedExternalUserPendingRegistrationNotification(ExternalUserEntity user);

}
