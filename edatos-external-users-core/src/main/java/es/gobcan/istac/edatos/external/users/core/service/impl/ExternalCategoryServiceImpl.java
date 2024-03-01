package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.List;

import org.siemac.metamac.rest.structural_resources.v1_0.domain.Categories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalCategoryEntity;
import es.gobcan.istac.edatos.external.users.core.mapper.ItemResourceMapper;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalCategoryRepository;
import es.gobcan.istac.edatos.external.users.core.service.EDatosApisLocator;
import es.gobcan.istac.edatos.external.users.core.service.ExternalCategoryService;

@Service
public class ExternalCategoryServiceImpl implements ExternalCategoryService {

    /**
     * Amount of resources that can be obtained in a request.
     */
    private static final String LIMIT = String.valueOf(Integer.MAX_VALUE);
    private static final Logger LOG = LoggerFactory.getLogger(ExternalCategoryServiceImpl.class);

    private final EDatosApisLocator eDatosApisLocator;
    private final ItemResourceMapper itemResourceMapper;
    private final ExternalCategoryRepository externalCategoryRepository;

    public ExternalCategoryServiceImpl(EDatosApisLocator eDatosApisLocator, ItemResourceMapper itemResourceMapper, ExternalCategoryRepository externalCategoryRepository) {
        this.eDatosApisLocator = eDatosApisLocator;
        this.itemResourceMapper = itemResourceMapper;
        this.externalCategoryRepository = externalCategoryRepository;
    }

    @Override
    public List<ExternalCategoryEntity> requestAllExternalCategoriesByUrn(List<String> urns) {
        LOG.info("Making request to Structural Resources Manager to GET all the categories by URN");
        String query = (urns != null && urns.size() > 0) ? String.format("URN IN (\"%s\")", String.join("\",\"", urns)) : null;
        Categories categories = eDatosApisLocator.srmExternal().findCategories("~all", "~all", "~all", query, null, LIMIT, null, null);
        return itemResourceMapper.toExternalCategoryEntities(categories);
    }

    @Override
    @Cacheable(cacheManager = "requestScopedCacheManager", cacheNames = "externalCategories")
    public List<ExternalCategoryEntity> requestAllExternalCategories() {
        LOG.info("Making request to Structural Resources Manager to GET all the categories");
        Categories categories = eDatosApisLocator.srmExternal().findCategories("~all", "~all", "~all", null, null, LIMIT, null, null);
        return itemResourceMapper.toExternalCategoryEntities(categories);
    }

    @Override
    public Page<ExternalCategoryEntity> requestExternalCategories(Pageable pageable, String search) {
        LOG.info("Making request to Structural Resources Manager to GET a page of categories. Page: {}, Search: {}", pageable, search);
        String sort = null;
        if (pageable.getSort() != null) {
            sort = pageable.getSort().toString().replace(": ", " ");
        }
        Categories categories = eDatosApisLocator.srmExternal().findCategories("~all", "~all", "~all", getQuery(search), sort, String.valueOf(pageable.getPageSize()),
                String.valueOf(pageable.getOffset()), null);
        List<ExternalCategoryEntity> externalCategories = itemResourceMapper.toExternalCategoryEntities(categories);
        return new PageImpl<>(externalCategories, pageable, categories.getTotal().longValue());
    }

    @Override
    public List<ExternalCategoryEntity> findAll() {
        return externalCategoryRepository.findAll();
    }

    private String getQuery(String search) {
        if (search == null) {
            return null;
        }
        return String.format("NAME ILIKE '%1$s' OR URN ILIKE '%1$s' OR DESCRIPTION ILIKE '%1$s'", search);
    }
}
