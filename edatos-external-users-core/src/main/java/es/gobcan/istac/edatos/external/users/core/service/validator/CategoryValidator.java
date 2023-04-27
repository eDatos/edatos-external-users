package es.gobcan.istac.edatos.external.users.core.service.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.service.FavoriteService;

@Component
public class CategoryValidator extends AbstractValidator<CategoryEntity> {

    private final FavoriteService favoriteService;

    public CategoryValidator(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public void checkCategoriesWithSubscribers(CategoryEntity category) {
        Set<CategoryEntity> deletedCategories = category.flattened().collect(Collectors.toSet());
        for (CategoryEntity deletedCategory : deletedCategories) {
            if (!favoriteService.findByCategory(deletedCategory).isEmpty()) {
                throw new EDatosException(ServiceExceptionType.CANNOT_DELETE_CATEGORY_IF_IT_HAS_SUBSCRIBERS);
            }
        }
    }

    public void checkCategoriesWithSubscribers(List<CategoryEntity> tree, List<CategoryEntity> allCategories) {
        Set<CategoryEntity> updatedCategories = tree.stream().flatMap(CategoryEntity::flattened).collect(Collectors.toSet());

        // Get categories that are no longer present in the tree but are in the database
        Set<CategoryEntity> deletedCategories = new HashSet<>(allCategories);
        deletedCategories.removeIf(c1 -> updatedCategories.stream().anyMatch(c2 -> Objects.equals(c1.getId(), c2.getId())));

        for (CategoryEntity category : deletedCategories) {
            if (!favoriteService.findByCategory(category).isEmpty()) {
                throw new EDatosException(ServiceExceptionType.CANNOT_DELETE_CATEGORY_IF_IT_HAS_SUBSCRIBERS);
            }
        }
    }
}
