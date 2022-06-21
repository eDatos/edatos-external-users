package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;

public interface FavoriteService {

    FavoriteEntity create(FavoriteEntity favorite);
    FavoriteEntity update(FavoriteEntity favorite);
    List<FavoriteEntity> updateFavorites(List<FavoriteEntity> favorite, ExternalUserEntity externalUser);
    FavoriteEntity find(Long id);
    List<FavoriteEntity> findAll();
    List<FavoriteEntity> findAllByUser(ExternalUserEntity user);
    Page<FavoriteEntity> find(String query, Pageable pageable);
    List<FavoriteEntity> find(String query, Sort sort);
    void delete(FavoriteEntity favorite);
    Map<String, Long> getCategorySubscribers();
    Map<String, Long> getOperationSubscribers();
    void deleteBySuscription(CategoryEntity category);
    List<FavoriteEntity> findByExternalUser();
    List<FavoriteEntity> findByCategory(CategoryEntity category);
    void deleteBySuscription(ExternalOperationEntity operation);
    List<FavoriteEntity> findByExternalOperation(List<ExternalOperationEntity> listExternalOperation);
}
