package es.gobcan.istac.edatos.external.users.rest.internal.conf;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import es.gobcan.istac.edatos.external.users.core.config.Constants;

@Configuration
public class LocaleConfiguration extends WebMvcConfigurerAdapter {

    public static class LocaleCheckInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
            String lang = request.getParameter(Constants.NAME_ATTRIBUTE_LANG);

            if (lang == null) {
                Cookie langCookie = WebUtils.getCookie(request, Constants.NAME_ATTRIBUTE_LANG);
                if (langCookie != null && !isValidLang(langCookie.getValue())) {
                    this.redirect(request, response);
                }
            } else if (!isValidLang(lang)) {
                this.redirect(request, response);
            } else {
                Locale.setDefault(Locale.forLanguageTag(lang));
            }

            return true;
        }

        private void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.sendRedirect(UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).queryParam(Constants.NAME_ATTRIBUTE_LANG, Constants.DEFAULT_LANG).build().toString());
        }

        private Boolean isValidLang(String lang) {
            return Constants.AVAILABLE_LANGS.contains(lang);
        }
    }

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        CookieLocaleResolver r = new CookieLocaleResolver();
        r.setDefaultLocale(Constants.DEFAULT_LOCALE);
        r.setCookieName(Constants.NAME_ATTRIBUTE_LANG);
        r.setCookieMaxAge(86400); // 24 * 60 * 60
        r.setCookieHttpOnly(false);
        return r;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(Constants.NAME_ATTRIBUTE_LANG);
        registry.addInterceptor(new LocaleCheckInterceptor());
        registry.addInterceptor(localeChangeInterceptor);
    }
}
