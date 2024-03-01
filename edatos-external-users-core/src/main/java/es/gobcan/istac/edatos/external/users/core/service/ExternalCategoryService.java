package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalCategoryEntity;

public interface ExternalCategoryService {

    List<ExternalCategoryEntity> requestAllExternalCategoriesByUrn(List<String> urns);
    List<ExternalCategoryEntity> requestAllExternalCategories();
    Page<ExternalCategoryEntity> requestExternalCategories(Pageable pageable, String search);
    List<ExternalCategoryEntity> findAll();
}
