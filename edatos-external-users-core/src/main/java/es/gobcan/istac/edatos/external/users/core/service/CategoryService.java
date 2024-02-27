package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalCategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;

public interface CategoryService {

    List<CategoryEntity> findAll();
    CategoryEntity findCategoryById(Long id);
    Page<CategoryEntity> find(String query, Pageable pageable);
    Page<CategoryEntity> find(DetachedCriteria criteria, Pageable pageable);
    List<CategoryEntity> getTree();
    Map<CategoryEntity, List<ExternalCategoryEntity>> getCategoryExternalCategories();
    List<CategoryEntity> updateTree(List<CategoryEntity> tree);
    CategoryEntity updateCategory(CategoryEntity category);
    CategoryEntity createCategory(CategoryEntity category);
    Optional<CategoryEntity> delete(Long id);
    CategoryEntity findCategoryByExternalOperation(ExternalOperationEntity externalOperation);
}
