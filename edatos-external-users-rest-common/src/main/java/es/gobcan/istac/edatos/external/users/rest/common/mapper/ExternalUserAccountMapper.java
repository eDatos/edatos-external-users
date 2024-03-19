package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.domain.DataProtectionPolicyEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.ExternalUserRole;
import es.gobcan.istac.edatos.external.users.core.repository.DataProtectionPolicyRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountBaseDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;
import es.gobcan.istac.edatos.external.users.rest.common.util.FormatDateUtil;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class})
public abstract class ExternalUserAccountMapper implements EntityMapper<ExternalUserAccountDto, ExternalUserEntity> {

    @Autowired
    private ExternalUserRepository externalUserRepository;
    @Autowired
    private DataProtectionPolicyRepository dataProtectionPolicyRepository;

    @Override
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "dataProtectionPolicy", source = "dataProtectionPolicyId", qualifiedByName = "protectionPolicyFromId")
    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayStringToLocalDate")
    @Mapping(target = "email", source = "email", qualifiedByName = "formatEmail")
    public abstract ExternalUserEntity toEntity(ExternalUserAccountDto dto);

    @Named("encodePassword")
    public String encodePassword(String unencryptedPassword) {
        return SecurityUtils.passwordEncoder(unencryptedPassword);
    }

    @AfterMapping
    public void afterMappingSettings(ExternalUserAccountBaseDto dto, @MappingTarget ExternalUserEntity entity) {
        Set<ExternalUserRole> roles = new HashSet<>();
        roles.add(ExternalUserRole.USER);
        roles.stream().collect(Collectors.toSet());
        entity.setRoles(roles);

        if (entity.getDataProtectionPolicyApprovalDate() != null) {
            entity.setDataProtectionPolicyApprovalDate(entity.getDataProtectionPolicyApprovalDate());
        } else if (entity.getDataProtectionPolicy() != null) {
            entity.setDataProtectionPolicyApprovalDate(Instant.now());
        }

        if (entity.getId() != null) {
            externalUserRepository.findOneById(entity.getId()).ifPresent(databaseUser -> {
                entity.setEmailModificationRequest(databaseUser.getEmailModificationRequest());
                entity.setEmail(databaseUser.getEmail());
            });
        }

        if (entity.getId() == null) {
            entity.setEmailNotificationsEnabled(true);
        }
    }

    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayLocalDateToString")
    public abstract ExternalUserAccountDto toDto(ExternalUserEntity entity);

    public ExternalUserEntity getUserFromEmail(String email) {
        return externalUserRepository.findOneByEmailIgnoreCase(email).orElse(null);
    }

    @Named("externalUserToBaseDto")
    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayLocalDateToString")
    public abstract ExternalUserAccountBaseDto toBaseDto(ExternalUserEntity entity);


    @Mapping(target = "birthday", source = "birthday", qualifiedByName = "formatBirthdayStringToLocalDate")
    @Mapping(target = "email", source = "email", qualifiedByName = "formatEmail")
    public abstract ExternalUserEntity baseDtoToEntity(ExternalUserAccountBaseDto dto);

    @Named("protectionPolicyFromId")
    public DataProtectionPolicyEntity protectionPolicyFromId(Long id) {
        if (id == null) {
            return null;
        }
        return dataProtectionPolicyRepository.findOne(id);
    }

    @Named("formatEmail")
    public String formatEmail(String email){
        return (email == null) ? null : email.toLowerCase();
    }

    @Named("formatBirthdayStringToLocalDate")
    public LocalDate formatBirthdayStringToLocalDate(String date){
        return FormatDateUtil.formatBirthdayStringToLocalDate(date);
    }

    @Named("formatBirthdayLocalDateToString")
    public String formatBirthdayLocalDateToString(LocalDate date){
        return FormatDateUtil.formatBirthdayLocalDateToString(date);
    }
}
