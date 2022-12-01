package es.gobcan.istac.edatos.external.users.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;

import java.util.List;
import java.util.Optional;

public interface ExternalUserService {

    ExternalUserEntity create(ExternalUserEntity user);

    ExternalUserEntity update(ExternalUserEntity user);

    //ExternalUserEntity deactivate(Long id);

    void delete(Long id);

    //ExternalUserEntity recover(Long id);

    ExternalUserEntity find(Long id);

    Optional<ExternalUserEntity> findByEmail(String email);

    Page<ExternalUserEntity> find(Pageable pageable, Boolean includeDeleted, String query);

    ExternalUserEntity getUsuarioWithAuthorities();
    void updateExternalUserAccountPassword(ExternalUserEntity user, String oldPassword, String password);

    void logout(String token);

    Optional<ExternalUserEntity> recoverExternalUserAccountPassword(String email);
    Optional<ExternalUserEntity> completePasswordReset(String newPassword, String key);
    Optional<ExternalUserEntity> findOneByResetKey(String key);

    ExternalUserEntity confirmUserRegistration(String token);

    List<ExternalUserEntity> findByCategoriesAndExternalOperations(List<Long> categoriesIds, List<Long> operationsIds);
}
