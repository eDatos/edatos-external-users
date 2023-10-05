package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;
import es.gobcan.istac.edatos.external.users.rest.common.util.FormatDateUtil;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses= {GenericMapperResolver.class, ExternalUserRepository.class})
public abstract class ExternalUserMapper implements EntityMapper<ExternalUserDto, ExternalUserEntity> {
    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayLocalDateToString")
    public abstract ExternalUserDto toDto(ExternalUserEntity entity);

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayStringToLocalDate")
    public abstract ExternalUserEntity toEntity(ExternalUserDto dto);


    @Named("formatBirthdayStringToLocalDate")
    public LocalDate formatBirthdayStringToLocalDate(String date){
        return FormatDateUtil.formatBirthdayStringToLocalDate(date);
    }

    @Named("formatBirthdayLocalDateToString")
    public String formatBirthdayLocalDateToString(LocalDate date){
        return FormatDateUtil.formatBirthdayLocalDateToString(date);
    }
}
