package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.TargetType;
import org.siemac.metamac.statistical.resources.core.stream.messages.DatasetVersionAvro;
import org.siemac.metamac.statistical.resources.core.stream.messages.InternationalStringAvro;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalDatasetRepository;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalDatasetDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.ibestat.jaxi.stream.messages.DatasetAvro;

@Mapper(componentModel = "spring",
        config = AuditingMapperConfig.class,
        uses = {InternationalStringVOMapper.class, ExternalOperationMapper.class})
public abstract class ExternalDatasetMapper implements EntityMapper<ExternalDatasetDto, ExternalDatasetEntity> {

    @Autowired
    ExternalDatasetRepository externalDatasetRepository;

    @Mapping(target = "name",
             source = "siemacMetadataStatisticalResource.lifecycleStatisticalResource.versionableStatisticalResource.nameableStatisticalResource.title")
    @Mapping(target = "urn",
             source = "siemacMetadataStatisticalResource.lifecycleStatisticalResource.versionableStatisticalResource.nameableStatisticalResource.identifiableStatisticalResource.urn")
    @Mapping(target = "code",
             source = "siemacMetadataStatisticalResource.lifecycleStatisticalResource.versionableStatisticalResource.nameableStatisticalResource.identifiableStatisticalResource.code")
    @Mapping(target = "externalOperationUrn",
             source = "siemacMetadataStatisticalResource.lifecycleStatisticalResource.versionableStatisticalResource.nameableStatisticalResource.identifiableStatisticalResource.statisticalOperation.urn")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "optLock", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    public abstract ExternalDatasetEntity toEntity(DatasetVersionAvro value);

    @ObjectFactory
    public ExternalDatasetEntity resolver(DatasetVersionAvro dataset, @TargetType Class<ExternalDatasetEntity> type) {
        if (dataset == null) {
            return new ExternalDatasetEntity();
        }
        String urn = dataset.getSiemacMetadataStatisticalResource()
                            .getLifecycleStatisticalResource()
                            .getVersionableStatisticalResource()
                            .getNameableStatisticalResource()
                            .getIdentifiableStatisticalResource()
                            .getUrn();
        if (urn == null) {
            return new ExternalDatasetEntity();
        }
        return externalDatasetRepository.findByUrn(urn).orElseGet(ExternalDatasetEntity::new);
    }
    
    @Mapping(target = "name",
            source = "title")
   @Mapping(target = "urn",
            source = "urn")
   @Mapping(target = "code",
            source = "code")
   @Mapping(target = "externalOperationUrn",
            source = "statisticalOperation.urn")
   @Mapping(target = "id", ignore = true)
   @Mapping(target = "type", ignore = true)
   @Mapping(target = "optLock", ignore = true)
   @Mapping(target = "createdDate", ignore = true)
   public abstract ExternalDatasetEntity toEntity(DatasetAvro value);
    
    @Mapping(target = "texts", source = "localisedStrings")
    public abstract InternationalStringVO toVO(InternationalStringAvro source);
    
    @ObjectFactory
    public ExternalDatasetEntity resolver(DatasetAvro dataset, @TargetType Class<ExternalDatasetEntity> type) {
        if (dataset == null) {
            return new ExternalDatasetEntity();
        }
        String urn = dataset.getUrn();
        if (urn == null) {
            return new ExternalDatasetEntity();
        }
        return externalDatasetRepository.findByUrn(urn).orElseGet(ExternalDatasetEntity::new);
    }
}
