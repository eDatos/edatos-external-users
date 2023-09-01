package es.gobcan.istac.edatos.external.users.core.mapper;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.siemac.edatos.core.common.util.GeneratorUrnUtils;
import org.siemac.metamac.rest.common.v1_0.domain.LocalisedString;
import org.siemac.metamac.rest.structural_resources.v1_0.domain.Code;
import org.springframework.beans.factory.annotation.Autowired;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.ExternalUserVO;
import es.gobcan.istac.edatos.external.users.core.service.EDatosApisLocator;

@Mapper(componentModel = "spring")
public abstract class ExternalUserVOMapper {

    @Autowired
    private EDatosApisLocator eDatosApisLocator;

    @Autowired
    private MetadataProperties metadataProperties;

    @Mapping(target = "gender", source = "entity", qualifiedByName = "getGender")
    @Mapping(target = "nationality", source = "entity", qualifiedByName = "getNationality")
    @Mapping(target = "birthday", source = "entity", qualifiedByName = "formatBirthday")
    public abstract ExternalUserVO toDto(ExternalUserEntity entity);

    @Named("getGender")
    public String getGender(ExternalUserEntity entity) {
        if (StringUtils.isNotEmpty(entity.getGender())) {
            Locale locale = LocaleContext.getLocale();
            String language = locale.toString().split("_")[0];

            String urn = metadataProperties.getGenderUrnKey();
            String[] dataStructureComponents = GeneratorUrnUtils.extractVersionableArtefactParts(urn);
            String agencyId = dataStructureComponents[0];
            String resourceId = dataStructureComponents[1];
            String version = dataStructureComponents[2];

            Code code = eDatosApisLocator.srmExternal().retrieveCode(agencyId, resourceId, version, entity.getGender(), null);
            for (LocalisedString localisedString : code.getName().getTexts()) {
                if (language.equalsIgnoreCase(localisedString.getLang())) {
                    return localisedString.getValue();
                }
            }
        }
        return null;
    }

    @Named("getNationality")
    public String getNationality(ExternalUserEntity entity) {
        if (StringUtils.isNotEmpty(entity.getNationality())) {
            Locale locale = LocaleContext.getLocale() == null ? LocaleContext.getDefaultLocale() : LocaleContext.getLocale();
            String language = locale.toString().split("_")[0];

            String urn = metadataProperties.getNationalityUrnKey();

            String[] dataStructureComponents = GeneratorUrnUtils.extractVersionableArtefactParts(urn);
            String agencyId = dataStructureComponents[0];
            String resourceId = dataStructureComponents[1];
            String version = dataStructureComponents[2];

            Code code = eDatosApisLocator.srmExternal().retrieveCode(agencyId, resourceId, version, entity.getNationality(), null);
            for (LocalisedString localisedString : code.getName().getTexts()) {
                if (language.equalsIgnoreCase(localisedString.getLang())) {
                    return localisedString.getValue();
                }
            }
        }
        return null;
    }

    @Named("formatBirthday")
    public String formatBirthday(ExternalUserEntity entity) {
        if (entity.getBirthday() != null) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(entity.getBirthday());
        }
        return null;
    }

}
