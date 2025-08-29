package es.gobcan.istac.edatos.external.users;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;

@Controller
public class DefaultController {

    @Autowired
    private MetadataProperties metadataProperties;

    @Autowired
    private MetadataConfigurationService metadataService;

    private final Logger log = LoggerFactory.getLogger(DefaultController.class);

    private String faviconUrl;

    @PostConstruct
    public void init() {
        this.faviconUrl = metadataService.retrieveAppStyleFaviconUrl();
    }

    @RequestMapping(value = {"", "/index.html", "/**/{path:[^\\.]*}"})
    @SuppressWarnings("unchecked")
    public ModelAndView index(HttpServletRequest request) {
        log.debug("DefaultController: Contextpath = {}  ServletPath = {}", request.getContextPath(), request.getServletPath());
        Map<String, Object> model = new HashMap<>();
        model.put("faviconUrl", this.faviconUrl);
        model.put("language", LocaleContext.getLocale() != null ? LocaleContext.getLocale().toString() : LocaleContext.getDefaultLocale().toString());
        model.put("timeZone", ZonedDateTime.now(ZoneId.systemDefault()).getOffset().getId());

        Map<String, Object> flashMap = (Map<String, Object>) RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null) {
            model.putAll(flashMap);
        }
        return new ModelAndView("index", model);
    }

}
