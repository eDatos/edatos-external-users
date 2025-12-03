package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalOperationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalCategoryEntity;
import es.gobcan.istac.edatos.external.users.core.service.CategoryService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.CategoryDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.CategoryMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping(CategoryResource.BASE_URL)
public class CategoryResource extends AbstractResource {

    public static final String ENTITY_NAME = "category";
    public static final String BASE_URL = "/api/categories";

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final ExternalOperationService externalOperationService;
    private final ExternalOperationMapper externalOperationMapper;

    public CategoryResource(CategoryService categoryService,
        CategoryMapper categoryMapper,
        ExternalOperationService externalOperationService,
        ExternalOperationMapper externalOperationMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.externalOperationService = externalOperationService;
        this.externalOperationMapper = externalOperationMapper;
    }

    @Timed
    @RequestMapping(value = "/exists", method = RequestMethod.HEAD)
    public ResponseEntity<Void> isTheOperationIncludedInAnyCategory(@RequestParam("operationCode") String operationCode) {
        ExternalOperationEntity operation = externalOperationService.findByCode(operationCode);
        if (operation != null && operation.isEnabled()) {
            List<CategoryEntity> operationAncestors = categoryService.findOperationAncestors(operation);
            if (!operationAncestors.isEmpty()) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessCategory(authentication)")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryEntity category = categoryService.findCategoryById(id);
        CategoryDto externalCategoryDto = categoryMapper.toDto(category);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(externalCategoryDto));
    }

    @GetMapping
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessCategory(authentication)")
    public ResponseEntity<List<CategoryDto>> getCategory(Pageable pageable, @RequestParam(required = false) String query) {
        Page<CategoryDto> result = categoryService.find(query, pageable).map(categoryMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(result, BASE_URL);
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }

    @Timed
    @GetMapping("/tree")
    @PreAuthorize("@secCheckerExternal.canAccessCategory(authentication)")
    public ResponseEntity<List<CategoryDto>> getCategoryTree() {
        List<CategoryEntity> tree = categoryService.getTree();
        List<CategoryEntity> all = categoryService.findAll();
        Map<CategoryEntity, List<ExternalCategoryEntity>> externalCategories = categoryService.getCategoryExternalCategories();
        List<ExternalOperationEntity> externalOperations = externalOperationService.findAllEnabled();
        return ResponseEntity.ok(categoryMapper.toDtos(all, tree, externalCategories, externalOperations, new HashMap<>()));
    }

    @Timed
    @PutMapping("/tree")
    @PreAuthorize("@secCheckerExternal.canAccessCategory(authentication)")
    public ResponseEntity<List<CategoryDto>> updateCategoryTree(@RequestBody List<CategoryDto> tree) {
        List<CategoryEntity> categories = categoryMapper.toEntities(tree);
        List<CategoryDto> response = categoryMapper.toDtos(categoryService.updateTree(categories));
        return ResponseEntity.ok(response);
    }
}
