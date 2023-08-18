package es.gobcan.istac.edatos.external.users.rest.internal.conf;

import es.gobcan.istac.edatos.external.users.core.config.Constants;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.siemac.edatos.core.common.util.shared.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.siemac.edatos.core.common.util.AbstractInternationalizationUtils.ACCEPT_LANGUAGE_HEADER;

@Configuration
public class LocaleConfiguration extends WebMvcConfigurerAdapter {

    private final MetadataProperties metadataProperties;

    public LocaleConfiguration(MetadataProperties metadataProperties) {
        this.metadataProperties = metadataProperties;
    }

    public class LocaleCheckInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String requestLocale = request.getLocale().getLanguage() == null ? null : StringUtils.substring(request.getLocale().getLanguage(), 0, 2);
            if (request.getHeader(ACCEPT_LANGUAGE_HEADER) != null && requestLocale != null && isValidLang(requestLocale)) {
                LocaleContext.setLocale(Locale.forLanguageTag(requestLocale));
            }

            LocaleContext.setDefaultLocale(Locale.forLanguageTag(metadataProperties.getDefaultLanguage()));
            Locale.setDefault(Locale.forLanguageTag(metadataProperties.getDefaultLanguage()));

            return true;
        }

        private Boolean isValidLang(String lang) {
            return Constants.AVAILABLE_LANGS.contains(lang);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleCheckInterceptor());
    }
}
