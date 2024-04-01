package es.gobcan.istac.edatos.external.users.core.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.domain.vo.AuditEventVO;
import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AuditEventMapper{

    private final static String FILTER = "FILTER";
    private final static String FAVORITE = "FAVORITE";

    @Autowired
    private ObjectMapper objectMapper;

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
        return entity.getData().values().stream().collect(Collectors.toList()).get(0).toString();
    }

    private String getFilterValue(AuditEvent entity) {
        return (String) entity.getData().getOrDefault(AuditConstants.MESSAGE, entity.getData().get(AuditConstants.CODE));
    }

    private String getFavoriteValue(AuditEvent entity) throws JsonProcessingException {
        String locale = LocaleContext.getLocale() == null ? null : LocaleContext.getLocale().toString();
        String defaultLocale = LocaleContext.getDefaultLocale().toString();

        InternationalStringVO name = objectMapper.readValue((String) entity.getData().get(AuditConstants.MESSAGE), InternationalStringVO.class); // TODO cuidado con valores previos
        return name.getLocalisedLabel(locale, defaultLocale);
    }
}
