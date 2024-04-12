package es.gobcan.istac.edatos.external.users.core.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.AuditEventVO;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalOperationRepository;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;

import java.util.ArrayList;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class AuditEventMapper{

    private final static String FILTER = "FILTER";
    private final static String FAVORITE = "FAVORITE";

    @Autowired
    private ExternalOperationRepository externalOperationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Mapping(target = "data", source = "entity", qualifiedByName = "getValue")
    public abstract AuditEventVO toDto(AuditEvent entity);

    @Named("getValue")
    public String getValue(AuditEvent entity) throws JsonProcessingException {

        if (entity.getType().contains(FILTER)) {
            return getFilterValue(entity);
        }
        if (entity.getType().contains(FAVORITE)) {
            return getFavoriteValue(entity);
        }
        return entity.getData().isEmpty() ? "" : new ArrayList<>(entity.getData().values()).get(0).toString();
    }

    private String getFilterValue(AuditEvent entity) {
        return (String) entity.getData().getOrDefault(AuditConstants.MESSAGE, entity.getData().get(AuditConstants.CODE));
    }

    private String getFavoriteValue(AuditEvent entity) {
        String locale = LocaleContext.getLocale() == null ? null : LocaleContext.getLocale().toString();
        String defaultLocale = LocaleContext.getDefaultLocale().toString();

        String code = entity.getData().getOrDefault(AuditConstants.CODE, "").toString();
        String subtype = (String) entity.getData().get(AuditConstants.EVENT_SUBTYPE);
        String resourceCode = (String) entity.getData().get(AuditConstants.MESSAGE);
        if (StringUtils.isNotBlank(resourceCode)) {
            if (AuditConstants.FAVORITE_OPERATION_SUBTYPE.equals(subtype)) {
                Optional<ExternalOperationEntity> operationOptional = externalOperationRepository.findByCode(resourceCode);
                return operationOptional.map(operation -> operation.getName().getLocalisedLabel(locale, defaultLocale)).orElse(code);
            } else if (AuditConstants.FAVORITE_CATEGORY_SUBTYPE.equals(subtype)) {
                Optional<CategoryEntity> categoryOptional = categoryRepository.findByCode(resourceCode);
                return categoryOptional.map(category -> category.getName().getLocalisedLabel(locale, defaultLocale)).orElse(code);
            }
        }
        return code;
    }
}
