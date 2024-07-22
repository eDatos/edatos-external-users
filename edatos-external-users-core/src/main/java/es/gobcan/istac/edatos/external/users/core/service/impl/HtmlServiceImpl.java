package es.gobcan.istac.edatos.external.users.core.service.impl;

import es.gobcan.istac.edatos.external.users.core.service.HtmlService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class HtmlServiceImpl implements HtmlService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MetadataConfigurationService metadataService;

    @Override
    public String getHeaderUrl() {
        Locale locale = LocaleContext.getLocale();
        String navbarUrl = metadataService.retrieveAppStyleHeaderUrl();
        String appName = messageSource.getMessage("global.title", null, locale);
        String appId = "external-users-EXTERNAL";

        return String.format("%s?appName=%s&appId=%s", navbarUrl, appName, appId);
    }
}
