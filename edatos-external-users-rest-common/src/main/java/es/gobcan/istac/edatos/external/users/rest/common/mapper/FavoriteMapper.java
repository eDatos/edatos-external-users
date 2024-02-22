package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.domain.*;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalOperationRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountBaseDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FavoriteDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class, ExternalUserAccountMapper.class, CategoryMapper.class})
public abstract class FavoriteMapper implements EntityMapper<FavoriteDto, FavoriteEntity> {

    @Autowired
    private ExternalUserRepository externalUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExternalOperationRepository externalOperationRepository;

    @Override
    @Mapping(target = "externalUser", source = "externalUser", qualifiedByName = "externalUserToBaseDto")
    public abstract FavoriteDto toDto(FavoriteEntity entity);

    @Override
    @Named(value = "mappingMethodForCollections")
    @Mapping(target = "externalUser", source = "externalUser.id", qualifiedByName = "externalUserFromId")
    @Mapping(target = "category", source = "category.id", qualifiedByName = "categoryFromId")
    @Mapping(target = "externalOperation", source = "externalOperation.id", qualifiedByName = "externalOperationFromId")
    public abstract FavoriteEntity toEntity(FavoriteDto dto);

    @Override
    @IterableMapping(qualifiedByName = "mappingMethodForCollections")
    public abstract List<FavoriteEntity> toEntities(List<FavoriteDto> dtos);

    @Named(value = "mappingMethodForCollectionsWithAuthenticatedUser")
    @Mapping(target = "externalUser", source = "externalUser", qualifiedByName = "externalUserFromAuthenticationEmail")
    @Mapping(target = "category", source = "category.id", qualifiedByName = "categoryFromId")
    @Mapping(target = "externalOperation", source = "externalOperation.id", qualifiedByName = "externalOperationFromId")
    public abstract FavoriteEntity toEntityWithAuthenticatedUser(FavoriteDto dto);

    @IterableMapping(qualifiedByName = "mappingMethodForCollectionsWithAuthenticatedUser")
    public abstract List<FavoriteEntity> toEntitiesWithAuthenticatedUser(List<FavoriteDto> dtos);

    @Named("externalUserFromId")
    public ExternalUserEntity externalUserFromId(Long id) {
        if (id == null) {
            return null;
        }
        return externalUserRepository.findOne(id);
    }

    @Named("externalUserFromAuthenticationEmail")
    public ExternalUserEntity externalUserFromAuthenticationEmail(ExternalUserAccountBaseDto externalUserDto) {
        String email = SecurityUtils.getCurrentUserLogin();
        if (email == null) {
            return null;
        }
        return externalUserRepository.findOneByEmailIgnoreCase(email).orElse(null);
    }

    @Named("categoryFromId")
    public CategoryEntity categoryFromId(Long id) {
        if (id == null) {
            return null;
        }
        return categoryRepository.findOne(id);
    }

    @Named("externalOperationFromId")
    public ExternalOperationEntity externalOperationFromId(Long id) {
        if (id == null) {
            return null;
        }
        return externalOperationRepository.findOne(id);
    }
}
