package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.config.Constants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalOperationRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FilterDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FilterWithOperationCodeDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class, ExternalUserMapper.class})
public abstract class FilterMapper implements EntityMapper<FilterDto, FilterEntity> {
    @Autowired
    private ExternalUserRepository externalUserRepository;

    @Autowired
    private ExternalOperationRepository externalOperationRepository;

    @Autowired
    private MetadataProperties metadataProperties;

    @Override
    @Mapping(target = "resourceName", ignore = true)
    @Mapping(target = "urlPermalink", source = "entity", qualifiedByName = "getUrlPermalink")
    public abstract FilterDto toDto(FilterEntity entity);

    @Override
    @Named(value = "mappingMethodForCollections")
    @Mapping(target = "name", defaultExpression = "java( dto.getResourceName() )")
    @Mapping(target = "lastAccessDate", defaultExpression = "java( java.time.Instant.now() )")
    @Mapping(target = "externalUser", source = "externalUser.id", qualifiedByName = "externalUserFromId")
    @Mapping(target = "externalOperation", ignore = true)
    @Mapping(target = "permalinkType", defaultValue = "VISUALIZER")
    public abstract FilterEntity toEntity(FilterDto dto);

    @Mapping(target = "name", defaultExpression = "java( dto.getResourceName() )")
    @Mapping(target = "lastAccessDate", defaultExpression = "java( java.time.Instant.now() )")
    @Mapping(target = "externalUser", source = "externalUser.id", qualifiedByName = "externalUserFromId")
    @Mapping(target = "externalOperation", source = "externalOperationCode", qualifiedByName = "externalOperationFromCode")
    @Mapping(target = "permalinkType", defaultValue = "VISUALIZER")
    public abstract FilterEntity toEntity(FilterWithOperationCodeDto dto);

    @Override
    @IterableMapping(qualifiedByName = "mappingMethodForCollections")
    public abstract List<FilterEntity> toEntities(List<FilterDto> dtos);

    @Named(value = "mappingMethodForCollectionsWithAuthenticatedUser")
    @Mapping(target = "name", defaultExpression = "java( dto.getResourceName() )")
    @Mapping(target = "lastAccessDate", defaultExpression = "java( java.time.Instant.now() )")
    @Mapping(target = "externalUser", source = "externalUser", qualifiedByName = "externalUserFromAuthenticationEmail")
    @Mapping(target = "externalOperation", ignore = true)
    @Mapping(target = "permalinkType", defaultValue = "VISUALIZER")
    public abstract FilterEntity toEntityWithAuthenticatedUser(FilterDto dto);

    @Mapping(target = "name", defaultExpression = "java( dto.getResourceName() )")
    @Mapping(target = "lastAccessDate", defaultExpression = "java( java.time.Instant.now() )")
    @Mapping(target = "externalUser", source = "externalUser", qualifiedByName = "externalUserFromAuthenticationEmail")
    @Mapping(target = "externalOperation", source = "externalOperationCode", qualifiedByName = "externalOperationFromCode")
    @Mapping(target = "permalinkType", defaultValue = "VISUALIZER")
    public abstract FilterEntity toEntityWithAuthenticatedUser(FilterWithOperationCodeDto dto);

    @IterableMapping(qualifiedByName = "mappingMethodForCollectionsWithAuthenticatedUser")
    public abstract List<FilterEntity> toEntitiesWithAuthenticatedUser(List<FilterDto> dtos);

    @Named("getUrlPermalink")
    public String getUrlPermalink(FilterEntity entity) {
        if (entity == null || entity.getPermalink() == null) {
            return null;
        }
        if (PermalinkType.VISUALIZER.equals(entity.getPermalinkType())) {
            return metadataProperties.getVisualizerPath() + Constants.PORTAL_PATH_PERMALINK + entity.getPermalink();
        } else if (PermalinkType.SIE.equals(entity.getPermalinkType())) {
            return metadataProperties.getSiePath() + Constants.SIE_PATH_PERMALINK + entity.getPermalink();
        } else {
            return entity.getPermalink();
        }
    }

    @Named("externalUserFromId")
    public ExternalUserEntity externalUserFromId(Long id) {
        if (id == null) {
            return null;
        }
        return externalUserRepository.findOne(id);
    }

    @Named("externalUserFromAuthenticationEmail")
    public ExternalUserEntity externalUserFromAuthenticationEmail(ExternalUserDto externalUserDto) {
        String email = SecurityUtils.getCurrentUserLogin();
        if (email == null) {
            return null;
        }
        return externalUserRepository.findOneByEmailIgnoreCase(email).orElse(null);
    }

    @Named("externalOperationFromCode")
    public ExternalOperationEntity externalOperationFromCode(String code) {
        if (code == null) {
            return null;
        }
        return externalOperationRepository.findByCode(code).orElse(null);
    }
}
