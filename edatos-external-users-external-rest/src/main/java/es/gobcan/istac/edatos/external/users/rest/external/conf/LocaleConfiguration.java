package es.gobcan.istac.edatos.external.users.rest.external.conf;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Configuration
public class LocaleConfiguration extends WebMvcConfigurerAdapter {

    private final MetadataProperties metadataProperties;

    public LocaleConfiguration(MetadataProperties metadataProperties) {
        this.metadataProperties = metadataProperties;
    }

    public class LocaleCheckInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            Cookie langCookie = metadataProperties.getLanguageCookie() != null ? WebUtils.getCookie(request, metadataProperties.getLanguageCookie()) : null;
            if (langCookie != null) {
                LocaleContext.setLocale(Locale.forLanguageTag(langCookie.getValue()));
            }
            LocaleContext.setDefaultLocale(Locale.forLanguageTag(metadataProperties.getDefaultLanguage()));

            return true;
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleCheckInterceptor());
    }
}
