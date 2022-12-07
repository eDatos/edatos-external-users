package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.ExternalUserRole;
import es.gobcan.istac.edatos.external.users.core.repository.DataProtectionPolicyRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountBaseDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserAccountDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class})
public abstract class ExternalUserAccountMapper implements EntityMapper<ExternalUserAccountDto, ExternalUserEntity> {

    @Autowired
    private ExternalUserRepository externalUserRepository;
    @Autowired
    private DataProtectionPolicyRepository dataProtectionPolicyRepository;


    @Override//target: nombre atribruto: de la entidad: dataProtectionPolicyEntity->Devuelve el metodo; source dataProtectionPolicyId -> se le pasa por parametro, qulify->nombreMetodo
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    public abstract ExternalUserEntity toEntity(ExternalUserAccountDto dto);

    @Named("encodePassword")
    public String encodePassword(String unencryptedPassword) {
        return SecurityUtils.passwordEncoder(unencryptedPassword);
    }

    @AfterMapping
    public void setRoles(ExternalUserAccountBaseDto dto, @MappingTarget ExternalUserEntity entity) {
        Set<ExternalUserRole> roles = new HashSet<>();
        roles.add(ExternalUserRole.USER);
        roles.stream().collect(Collectors.toSet());
        entity.setRoles(roles);
    }

    @Override
    @Mapping(target = "password", ignore = true)
    public abstract ExternalUserAccountDto toDto(ExternalUserEntity dto);

    public ExternalUserEntity getUserFromEmail(String email) {
        return externalUserRepository.findOneByEmail(email).orElse(null);
    }

    @Named("externalUserToBaseDto")
    public abstract ExternalUserAccountBaseDto toBaseDto(ExternalUserEntity entity);

    public abstract ExternalUserEntity baseDtoToEntity(ExternalUserAccountBaseDto dto);

    //target: nombre atribruto: de la entidad: dataProtectionPolicyEntity->Devuelve el metodo; source dataProtectionPolicyId -> se le pasa por parametro, qulify->nombreMetodo
    @Named("protectionPolicyFromId")
    public DataProtectionPolicyEntity protectionPolicyFromId(Long id) {
        if (id == null) {
            return null;
        }

        return dataProtectionPolicyRepository.findOne(id);
    }

}
