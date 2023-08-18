package es.gobcan.istac.edatos.external.users.core.service.validator;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import org.apache.commons.lang.StringUtils;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class InternationalStringValidator extends AbstractValidator<InternationalStringVO> {

    @Autowired
    private MetadataProperties metadataProperties;

    public void checkMandatoryLanguageIsSet(InternationalStringVO internationalString) {
        if (internationalString != null && StringUtils.isBlank(internationalString.getLocalisedLabel(metadataProperties.getDefaultLanguage(), null))) {
            String defaultLanguageName = Locale.forLanguageTag(metadataProperties.getDefaultLanguage()).getDisplayLanguage(Locale.getDefault()).toLowerCase();
            throw new EDatosException(ServiceExceptionType.MISSING_DEFAULT_LOCALE_TRANSLATION, defaultLanguageName);
        }
    }
}
