package es.gobcan.istac.edatos.external.users.core.service.impl;

import es.gobcan.istac.edatos.external.users.core.service.HtmlService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
        Locale locale = LocaleContextHolder.getLocale();
        String navbarUrl = metadataService.retrieveAppStyleHeaderUrl();
        String appName = messageSource.getMessage("global.title", null, locale);

        return String.format("%s?appName=%s", navbarUrl, appName);
    }
}
