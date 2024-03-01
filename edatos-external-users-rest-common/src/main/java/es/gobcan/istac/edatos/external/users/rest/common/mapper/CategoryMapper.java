package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalCategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalCategoryRepository;
import es.gobcan.istac.edatos.external.users.core.service.ExternalCategoryService;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import es.gobcan.istac.edatos.external.users.core.service.FavoriteService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.CategoryDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalCategoryDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalOperationDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {InternationalStringVOMapper.class, ExternalCategoryMapper.class})
public abstract class CategoryMapper implements EntityMapper<CategoryDto, CategoryEntity> {

    @Autowired
    FavoriteService favoriteService;

    @Autowired
    ExternalCategoryService externalCategoryService;

    @Autowired
    ExternalCategoryRepository externalCategoryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExternalCategoryMapper externalCategoryMapper;

    @Autowired
    ExternalOperationMapper externalOperationMapper;

    @Autowired
    ExternalOperationService externalOperationService;

    @Override
    @Mapping(target = "externalCategories", source = "dto.externalCategories", qualifiedByName = "getExternalCategoryEntitiesFromUrn")
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "getParentFromId")
    public abstract CategoryEntity toEntity(CategoryDto dto);

    public List<ExternalOperationDto> getExternalOperations(CategoryEntity category, Map<CategoryEntity, List<ExternalCategoryEntity>> categoriesExternalCategories,
            List<ExternalOperationEntity> externalOperations, Map<String, Long> subscribers) {
        List<ExternalCategoryDto> externalCategories = getExternalCategories(category, categoriesExternalCategories);
        return getOperations(externalOperations, externalCategories, subscribers);
    }

    @Named("getParentFromId")
    public CategoryEntity getParentFromId(Long parentId) {
        if (parentId != null && parentId > 0) {
            return categoryRepository.findOne(parentId);
        }
        return null;
    }

    @Named("getExternalCategoryEntitiesFromUrn")
    public Set<ExternalCategoryEntity> getExternalCategoryEntitiesFromUrn(List<ExternalCategoryDto> resources) {
        List<String> urns = resources.stream().map(ExternalCategoryDto::getUrn).filter(Objects::nonNull).collect(Collectors.toList());
        List<ExternalCategoryEntity> inDbExternalCategories = externalCategoryService.findAll();
        // @formatter:off
        return externalCategoryService.requestAllExternalCategoriesByUrn(urns).stream()
                // if the external category was already in db, pick that one; otherwise create a new one (this avoids problems with unique constraints and duplication)
                .map(newExternalCategory -> inDbExternalCategories.stream()
                                                                  .filter(inDbExternalCategory -> Objects.equals(inDbExternalCategory.getUrn(), newExternalCategory.getUrn()))
                                                                  .findAny()
                                                                  .orElse(newExternalCategory))
                .collect(Collectors.toSet());
        // @formatter:on
    }

    // -------------------------------------------------------
    // -------------------------------------------------------
    // -------------------------------------------------------

    public List<ExternalCategoryDto> getExternalCategories(CategoryEntity category, Map<CategoryEntity, List<ExternalCategoryEntity>> categoriesExternalCategories) {
        List<ExternalCategoryDto> list = new ArrayList<>();
        for (ExternalCategoryEntity entity : categoriesExternalCategories.getOrDefault(category, new ArrayList<>())) {
            ExternalCategoryDto externalCategoryDto = externalCategoryMapper.toDto(entity);
            list.add(externalCategoryDto);
        }
        return list;
    }

    public List<ExternalOperationDto> getOperations(List<ExternalOperationEntity> externalOperations, Collection<ExternalCategoryDto> entities, Map<String, Long> subscribers) {
        List<String> externalCategoriesUrns = entities.stream().map(ExternalCategoryDto::getUrn).collect(Collectors.toList());
        List<ExternalOperationEntity> adscribedExternalOperations = externalOperations.stream().filter(operation -> externalCategoriesUrns.contains(operation.getExternalCategoryUrn())).collect(Collectors.toList());
        return adscribedExternalOperations.stream().map(entity -> {
            ExternalOperationDto dto = externalOperationMapper.toDto(entity);
            dto.setSubscribers(subscribers.getOrDefault(entity.getUrn(), 0L));
            return dto;
        }).collect(Collectors.toList());
    }

    public List<CategoryEntity> getChildren(CategoryEntity category, List<CategoryEntity> allCategories) {
        List<CategoryEntity> list = new ArrayList<>();
        for (CategoryEntity cat : allCategories) {
            if (Objects.equals(cat.getParent(), category)) {
                list.add(cat);
            }
        }
        return list;
    }

    public List<CategoryDto> toDtos(List<CategoryEntity> allCategories, List<CategoryEntity> categories, Map<CategoryEntity, List<ExternalCategoryEntity>> categoryExternalCategories,
            List<ExternalOperationEntity> externalOperations, Map<String, Long> subscribers) {
        List<CategoryDto> list = new ArrayList<>();
        if (categories.isEmpty()) {
            return list;
        }
        for (CategoryEntity category : categories) {
            CategoryDto categoryDto = toDto(category, allCategories, categoryExternalCategories, externalOperations, subscribers);
            list.add(categoryDto);
        }
        return list;
    }

    @Mapping(target = "subscribers", expression = "java(subscribers.getOrDefault(category.getId().toString(), 0L))")
    @Mapping(target = "externalCategories", expression = "java(getExternalCategories(category, categoriesExternalCategories))")
    @Mapping(target = "externalOperations", expression = "java(getExternalOperations(category, categoriesExternalCategories, externalOperations, subscribers))")
    @Mapping(target = "children", expression = "java(toDtos(allCategories, getChildren(category, allCategories), categoriesExternalCategories, externalOperations, subscribers))")
    public abstract CategoryDto toDto(CategoryEntity category, List<CategoryEntity> allCategories, Map<CategoryEntity, List<ExternalCategoryEntity>> categoriesExternalCategories,
            List<ExternalOperationEntity> externalOperations, Map<String, Long> subscribers);
}
