package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;

public interface ExternalUserService {

    ExternalUserEntity create(ExternalUserEntity user);

    ExternalUserEntity update(ExternalUserEntity user);

    ExternalUserEntity delete(Long id);

    ExternalUserEntity find(Long id);

    Optional<ExternalUserEntity> findByEmail(String email);

    List<ExternalUserEntity> findAllByEmailIn(List<String> emails);

    Page<ExternalUserEntity> find(Pageable pageable, Boolean includeDeleted, String query);

    List<ExternalUserEntity> findAllReceivers();

    ExternalUserEntity getUsuarioWithAuthorities();

    Optional<ExternalUserEntity> createEmailModificationRequest(ExternalUserEntity user, String email);

    Optional<ExternalUserEntity> findByEmailModificationRequestKey(String key);

    Optional<ExternalUserEntity> confirmEmailModification(String key);

    void updateExternalUserAccountPassword(ExternalUserEntity user, String oldPassword, String password);

    void logout(String token);

    Optional<ExternalUserEntity> recoverExternalUserAccountPassword(String email);
    Optional<ExternalUserEntity> completePasswordReset(String newPassword, String key);
    Optional<ExternalUserEntity> findOneByResetKey(String key);

    ExternalUserEntity confirmUserRegistration(String token);
    ExternalUserEntity getByUserRegistration(String token);

    boolean isMigratedUser(ExternalUserEntity user);

    ExternalUserEntity migrate(ExternalUserEntity entity);

    Set<ExternalUserEntity> findByEmailOrFavoritesOrFilterOperationIn(List<String> emails, List<Long> favorites, List<Long> filtersExternalOperations);
}
