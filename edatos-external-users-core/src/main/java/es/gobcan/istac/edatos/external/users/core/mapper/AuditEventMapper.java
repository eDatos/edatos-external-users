package es.gobcan.istac.edatos.external.users.core.mapper;

import java.util.Locale;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.context.i18n.LocaleContextHolder;

import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.AuditEventVO;
import es.gobcan.istac.edatos.external.users.core.repository.FavoriteRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;

@Mapper(componentModel = "spring")
public abstract class AuditEventMapper{

    private final static String FILTER = "FILTER";
    private final static String FAVORITE = "FAVORITE";
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Mapping(target = "data", source = "entity", qualifiedByName = "getValue")
    public abstract AuditEventVO toDto(AuditEvent entity);

    @Named("getValue")
    public String getValue(AuditEvent entity) {

        if (entity.getType().contains(FILTER)) {
            return getFilterValue(entity);
        }
        if (entity.getType().contains(FAVORITE)) {
            return getFavoriteValue(entity);
        }
        return entity.getData().values().stream().collect(Collectors.toList()).get(0).toString();
    }

    private String getFilterValue(AuditEvent entity){
        String code = entity.getData().values().stream().collect(Collectors.toList()).get(0).toString();
        Long id = Long.valueOf(code).longValue();
        if(filterRepository.findOne(id) != null){
            return filterRepository.findOne(id).getName();
        }
        return code;
    }

    private String getFavoriteValue(AuditEvent entity){
        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.toString().split("_")[0];

        String code = entity.getData().values().stream().collect(Collectors.toList()).get(0).toString();
        Long id = Long.valueOf(code).longValue();
        FavoriteEntity favorite = favoriteRepository.findOne(id);
        if(favorite != null) {
            if (favorite.getCategory() != null) {
                return favorite.getCategory().getName().getLocalisedLabel(language);
            } else if (favorite.getExternalOperation() != null) {
                return favorite.getExternalOperation().getName().getLocalisedLabel(language);
            }
        }
        return code;
    }
}
