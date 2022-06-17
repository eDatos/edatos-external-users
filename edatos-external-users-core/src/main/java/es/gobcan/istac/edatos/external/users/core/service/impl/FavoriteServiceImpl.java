package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalItemEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalOperationRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FavoriteRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.core.service.FavoriteService;
import es.gobcan.istac.edatos.external.users.core.util.QueryUtil;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ExternalOperationRepository externalOperationRepository;
    private final ExternalUserRepository externalUserRepository;
    private final QueryUtil queryUtil;

    private final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ExternalOperationRepository externalOperationRepository, ExternalUserRepository externalUserRepository, QueryUtil queryUtil) {
        this.favoriteRepository = favoriteRepository;
        this.externalOperationRepository = externalOperationRepository;
        this.externalUserRepository = externalUserRepository;
        this.queryUtil = queryUtil;
    }

    @Override
    public FavoriteEntity create(FavoriteEntity favorite) {
        if (favorite.getCategory() != null) {
            create(favorite.getExternalUser(), favorite.getCategory());
            return favoriteRepository.findByExternalUserAndCategory(favorite.getExternalUser(), favorite.getCategory()).get();
        } else {
            return favoriteRepository.saveAndFlush(favorite);
        }
    }

    private void create(ExternalUserEntity externalUser, CategoryEntity parent) {
        if (favoriteRepository.findByExternalUser(externalUser).stream().map(FavoriteEntity::getCategory).noneMatch(cat -> Objects.equals(cat, parent))) {
            favoriteRepository.save(newFavorite(externalUser, parent));
        }
        List<String> urns = parent.getExternalCategories().stream().map(ExternalItemEntity::getUrn).collect(Collectors.toList());
        List<ExternalOperationEntity> operations = externalOperationRepository.findByExternalCategoryUrnIn(urns);
        for (ExternalOperationEntity operation : operations) {
            if (favoriteRepository.findByExternalUser(externalUser).stream().map(FavoriteEntity::getExternalOperation).noneMatch(op -> Objects.equals(op, operation))) {
                favoriteRepository.save(newFavorite(externalUser, operation));
            }
        }
        for (CategoryEntity child : parent.getChildren()) {
            create(externalUser, child);
        }
    }

    private FavoriteEntity newFavorite(ExternalUserEntity externalUser, CategoryEntity category) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setExternalUser(externalUser);
        favorite.setCategory(category);
        return favorite;
    }

    private FavoriteEntity newFavorite(ExternalUserEntity externalUser, ExternalOperationEntity operation) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setExternalUser(externalUser);
        favorite.setExternalOperation(operation);
        return favorite;
    }

    @Override
    public FavoriteEntity update(FavoriteEntity favorite) {
        return favoriteRepository.saveAndFlush(favorite);
    }

    @Override
    public List<FavoriteEntity> updateFavorites(List<FavoriteEntity> favorites, ExternalUserEntity externalUser) {
        for (FavoriteEntity favorite : favorites) {
            favorite.setExternalUser(externalUser);
        }
        favoriteRepository.deleteByExternalUser(externalUser);
        favoriteRepository.flush();
        return favoriteRepository.save(favorites);
    }

    @Override
    public FavoriteEntity find(Long id) {
        return favoriteRepository.findOne(id);
    }

    @Override
    public List<FavoriteEntity> findAll() {
        return favoriteRepository.findAll();
    }

    @Override
    public List<FavoriteEntity> findAllByUser(ExternalUserEntity user) {
        return favoriteRepository.findByExternalUser(user);
    }

    @Override
    public Page<FavoriteEntity> find(String query, Pageable pageable) {
        DetachedCriteria criteria = queryUtil.queryToFavoriteCriteria(query, pageable);
        return favoriteRepository.findAll(criteria, pageable);
    }

    @Override
    public List<FavoriteEntity> find(String query, Sort sort) {
        DetachedCriteria criteria = queryUtil.queryToFavoriteSortCriteria(query, sort);
        return favoriteRepository.findAll(criteria);
    }

    @Override
    public List<FavoriteEntity> findByExternalUser() {
        ExternalUserEntity externalUser = externalUserRepository.findOneByEmail(SecurityUtils.getCurrentUserLogin()).get();
        return favoriteRepository.findByExternalUser(externalUser);
    }

    @Override
    public Map<String, Long> getCategorySubscribers() {
        return favoriteRepository.getCategoriesSubscribers().stream().collect(Collectors.toMap(subscriber -> subscriber.getLeft().toString(), ImmutablePair::getRight));
    }

    @Override
    public void delete(FavoriteEntity favorite) {
        if (favorite.getCategory() != null) {
            delete(favorite.getExternalUser(), favorite.getCategory());
        } else {
            // operation favorites doesn't have children
            favoriteRepository.delete(favorite);
        }
    }

    @Override
    public List<FavoriteEntity> findByCategory(CategoryEntity category) {
        return this.favoriteRepository.findByCategory(category);
    }

    @Override
    public void deleteBySuscription(CategoryEntity category) {
        favoriteRepository.deleteByCategory(category);
    }

    @Override
    public void deleteBySuscription(ExternalOperationEntity operation) {
        favoriteRepository.deleteByExternalOperationUrn(operation.getUrn());
    }

    @Override
    @Cacheable(cacheManager = "requestScopedCacheManager", cacheNames = "operations")
    public Map<String, Long> getOperationSubscribers() {
        return favoriteRepository.getOperationsSubscribers().stream().collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public void delete(ExternalUserEntity externalUser, CategoryEntity parent) {
        favoriteRepository.deleteByExternalUserAndCategory(externalUser, parent);
        List<String> urns = parent.getExternalCategories().stream().map(ExternalItemEntity::getUrn).collect(Collectors.toList());
        List<ExternalOperationEntity> operations = externalOperationRepository.findByExternalCategoryUrnIn(urns);
        for (ExternalOperationEntity operation : operations) {
            favoriteRepository.deleteByExternalUserAndExternalOperation(externalUser, operation);
        }
        for (CategoryEntity child : parent.getChildren()) {
            delete(externalUser, child);
        }
    }

    @Override
    public List<FavoriteEntity> findByExternalOperation(List<ExternalOperationEntity> listExternalOperation) {
        return favoriteRepository.findByExternalOperationIn(listExternalOperation);
    }

    @Override
    public List<FavoriteEntity> findByOperationsIdsOrCategoriesIds(List<Long> operationsIds, List<Long> categoriesIds) {
        List<Long> nullSafeOperationsIds = operationsIds == null ? new ArrayList<>() : operationsIds;
        List<Long> nullSafeCategoriesIds = categoriesIds == null ? new ArrayList<>() : categoriesIds;
        return this.favoriteRepository.findByExternalOperation_idInOrCategory_idIn(nullSafeOperationsIds, nullSafeCategoriesIds);
    }
}
