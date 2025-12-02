package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;

public interface FavoriteService {

    FavoriteEntity create(FavoriteEntity favorite);
    List<FavoriteEntity> updateFavorites(List<FavoriteEntity> favorite, ExternalUserEntity externalUser);
    FavoriteEntity find(Long id);
    Page<FavoriteEntity> find(String query, Pageable pageable);
    void delete(FavoriteEntity favorite);
    Map<String, Long> getCategorySubscribers();
    Map<String, Long> getOperationSubscribers();
    List<FavoriteEntity> findByExternalUser();
    List<FavoriteEntity> findByCategory(CategoryEntity category);
    void deleteBySubscription(ExternalOperationEntity operation);
    List<FavoriteEntity> findByExternalOperationOrCategory(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories);
    List<FavoriteEntity> findByExternalOperationOrCategoryAndAncestors(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories);
    List<FavoriteEntity> findByExternalUserAndExternalOperationOrCategory(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories);
    List<FavoriteEntity> findByExternalUserAndExternalOperationOrCategoryAndAncestors(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories);
}
