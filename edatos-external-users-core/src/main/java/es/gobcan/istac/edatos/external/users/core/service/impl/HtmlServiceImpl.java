package es.gobcan.istac.edatos.external.users.core.service.impl;

import es.gobcan.istac.edatos.external.users.core.service.HtmlService;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
public class HtmlServiceImpl implements HtmlService {

    private final Logger log = LoggerFactory.getLogger(HtmlServiceImpl.class);

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

    @Override
    public String getFooterHtml() {
        return renderHtml(metadataService.retrieveAppStyleFooterUrl());
    }

    private String renderHtml(String urlToRead) {
        try {
            RestTemplate plantilla = new RestTemplate();
            return plantilla.getForObject(urlToRead, String.class);
        } catch (Exception e) {
            log.error("Error when obtaining HTML from URL: {}", urlToRead);
            return null;
        }
    }
}
