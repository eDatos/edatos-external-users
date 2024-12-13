package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import es.gobcan.istac.edatos.external.users.core.service.HtmlService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Service
public class HtmlServiceImpl implements HtmlService {

    @Autowired
    private MessageSource messageSource;
    private ResourceBundle resourceBundle;

    @Autowired
    private MetadataConfigurationService metadataService;

    @Override
    public String getHeaderUrl() {
        Locale locale = LocaleContext.getLocale();
        String navbarUrl = metadataService.retrieveAppStyleHeaderUrl();
        String appName = messageSource.getMessage("global.title", null, locale);
        String appId = "external-users-EXTERNAL";
        resourceBundle = ResourceBundle.getBundle("config.application");
        String appVersion = resourceBundle.getString("appVersion");

        return String.format("%s?appName=%s&appId=%s&appVersion=%s", navbarUrl, appName, appId, appVersion);
    }
}
