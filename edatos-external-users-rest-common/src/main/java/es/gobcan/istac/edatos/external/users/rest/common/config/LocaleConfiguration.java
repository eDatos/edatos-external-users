package es.gobcan.istac.edatos.external.users.rest.common.config;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

@Configuration
public class LocaleConfiguration extends WebMvcConfigurerAdapter {

    private final MetadataProperties metadataProperties;

    public LocaleConfiguration(MetadataProperties metadataProperties) {
        this.metadataProperties = metadataProperties;
    }

    public class LocaleCheckInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
            Cookie langCookie = WebUtils.getCookie(request, metadataProperties.getLanguageCookie());
            if (langCookie != null) {
                LocaleContextHolder.setLocale(Locale.forLanguageTag(langCookie.getValue()));
            }
            return true;
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleCheckInterceptor());
    }
}
