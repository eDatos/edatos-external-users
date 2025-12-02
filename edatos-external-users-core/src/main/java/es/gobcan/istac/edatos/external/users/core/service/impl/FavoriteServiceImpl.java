package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;
import es.gobcan.istac.edatos.external.users.core.service.CategoryService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.criterion.DetachedCriteria;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final QueryUtil queryUtil;

    private final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ExternalOperationRepository externalOperationRepository,
            ExternalUserRepository externalUserRepository, QueryUtil queryUtil, CategoryRepository categoryRepository, CategoryService categoryService) {
        this.favoriteRepository = favoriteRepository;
        this.externalOperationRepository = externalOperationRepository;
        this.externalUserRepository = externalUserRepository;
        this.queryUtil = queryUtil;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @Override
    public FavoriteEntity create(FavoriteEntity favorite) {
        if (existsAnEqualFavoriteOrWithAnAncestorCategory(favorite.getExternalUser(), favorite)) {
            throw new EDatosException(ServiceExceptionType.FAVORITE_CREATE_ANCESTOR_ALREADY_FAVORITE);
        } else {
            if (favorite.getCategory() != null) {
                deleteChildren(favorite.getExternalUser(), favorite.getCategory());
            }
            return favoriteRepository.save(favorite);
        }
    }

    private boolean existsAnEqualFavoriteOrWithAnAncestorCategory(ExternalUserEntity externalUser, FavoriteEntity favorite) {
        List<ExternalOperationEntity> favoriteOperation = favorite.getExternalOperation() != null ? Collections.singletonList(favorite.getExternalOperation()) : new ArrayList<>();
        return !favoriteRepository.findByExternalUserAndExternalOperationIn(externalUser, favoriteOperation).isEmpty() ||
            !favoriteRepository.findByExternalUserAndCategoryIn(externalUser, getCategoryAncestorsFromFavorite(favorite)).isEmpty();
    }

    private List<CategoryEntity> getCategoryAncestorsFromFavorite(FavoriteEntity favorite) {
        List<CategoryEntity> operationAncestors = new ArrayList<>();
        CategoryEntity operationAncestorCategory = favorite.getCategory() == null ?
            categoryRepository.findByExternalCategoriesUrn(favorite.getExternalOperation().getExternalCategoryUrn()) :
            categoryRepository.findOne(favorite.getCategory().getId());
        operationAncestors.add(operationAncestorCategory);
        while (operationAncestorCategory.getParent() != null) {
            operationAncestors.add(operationAncestorCategory.getParent());
            operationAncestorCategory = operationAncestorCategory.getParent();
        }
        return operationAncestors;
    }

    @Override
    public List<FavoriteEntity> updateFavorites(List<FavoriteEntity> favorites, ExternalUserEntity externalUser) {
        favoriteRepository.deleteByExternalUser(externalUser);
        favoriteRepository.flush();
        List<FavoriteEntity> dbFavorites = new ArrayList<>();
        for (FavoriteEntity favorite : favorites) {
            favorite.setExternalUser(externalUser);
            try {
                dbFavorites.add(create(favorite));
            } catch (EDatosException e) {
                InternationalStringVO internationalFavoriteName = favorite.getCategory() != null ? favorite.getCategory().getName() : favorite.getExternalOperation().getName();
                String favoriteName = internationalFavoriteName.getLocalisedLabel("en", LocaleContext.getDefaultLocale().toString());
                String favoriteType = favorite.getCategory() != null ? "category" : "operation";
                log.debug("The user " + externalUser.getEmail() + " can not create a favorite with the " + favoriteType + " " + favoriteName + " because there is already an ancestor marked as favorite.");
            }
        }
        return dbFavorites;
    }

    @Override
    public FavoriteEntity find(Long id) {
        return favoriteRepository.findOne(id);
    }

    @Override
    public Page<FavoriteEntity> find(String query, Pageable pageable) {
        DetachedCriteria criteria = queryUtil.queryToFavoriteCriteria(query, pageable);
        return favoriteRepository.findAll(criteria, pageable);
    }

    @Override
    public List<FavoriteEntity> findByExternalUser() {
        ExternalUserEntity externalUser = externalUserRepository.findOneByEmailIgnoreCase(SecurityUtils.getCurrentUserLogin()).get();
        return favoriteRepository.findByExternalUser(externalUser);
    }

    @Override
    public Map<String, Long> getCategorySubscribers() {
        return favoriteRepository.getCategoriesSubscribers().stream().collect(Collectors.toMap(subscriber -> subscriber.getLeft().toString(), ImmutablePair::getRight));
    }

    @Override
    public void delete(FavoriteEntity favorite) {
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<FavoriteEntity> findByCategory(CategoryEntity category) {
        return this.favoriteRepository.findByCategory(category);
    }

    @Override
    public void deleteBySubscription(ExternalOperationEntity operation) {
        favoriteRepository.deleteByExternalOperationUrn(operation.getUrn());
    }

    @Override
    @Cacheable(cacheManager = "requestScopedCacheManager", cacheNames = "operations")
    public Map<String, Long> getOperationSubscribers() {
        return favoriteRepository.getOperationsSubscribers().stream().collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    public void deleteChildren(ExternalUserEntity externalUser, CategoryEntity parent) {
        List<String> urns = parent.getExternalCategories().stream().map(ExternalItemEntity::getUrn).collect(Collectors.toList());
        List<ExternalOperationEntity> operations = externalOperationRepository.findByExternalCategoryUrnIn(urns);
        for (ExternalOperationEntity operation : operations) {
            favoriteRepository.deleteByExternalUserAndExternalOperation(externalUser, operation);
        }
        for (CategoryEntity child : parent.getChildren()) {
            deleteChildren(externalUser, child);
            favoriteRepository.deleteByExternalUserAndCategory(externalUser, child);
        }
    }

    @Override
    public List<FavoriteEntity> findByExternalOperationOrCategory(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories) {
        return favoriteRepository.findByExternalOperationInOrCategoryIn(listOperations, listCategories);
    }

    @Override
    public List<FavoriteEntity> findByExternalOperationOrCategoryAndAncestors(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories) {
        List<CategoryEntity> categoriesWithAncestors = getCategoriesIncludingAncestors(listOperations, listCategories);
        return findByExternalOperationOrCategory(listOperations, categoriesWithAncestors);
    }

    @Override
    public List<FavoriteEntity> findByExternalUserAndExternalOperationOrCategory(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories) {
        ExternalUserEntity externalUser = externalUserRepository.findOneByEmailIgnoreCase(SecurityUtils.getCurrentUserLogin()).get();

        return favoriteRepository.findByExternalUserAndExternalOperationInOrExternalUserAndCategoryIn(
            externalUser,
            listOperations,
            externalUser,
            listCategories
        );
    }

    @Override
    public List<FavoriteEntity> findByExternalUserAndExternalOperationOrCategoryAndAncestors(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories) {
        List<CategoryEntity> categoriesWithAncestors = getCategoriesIncludingAncestors(listOperations, listCategories);
        return findByExternalUserAndExternalOperationOrCategory(listOperations, categoriesWithAncestors);
    }

    private List<CategoryEntity> getCategoriesIncludingAncestors(List<ExternalOperationEntity> listOperations, List<CategoryEntity> listCategories) {
        Set<CategoryEntity> categoryEntities = new HashSet<>();
        for (ExternalOperationEntity operation : listOperations) {
            categoryEntities.addAll(categoryService.findOperationAncestors(operation));
        }
        for (CategoryEntity category : listCategories) {
            categoryEntities.addAll(categoryService.findCategoryAncestors(category));
        }
        return new ArrayList<>(categoryEntities);
    }
}
