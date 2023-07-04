package es.gobcan.istac.edatos.external.users.core.i18n;

import java.util.Locale;

public abstract class LocaleContext {

    private static Locale locale;
    private static Locale defaultLocale;

    public static Locale getLocale() {
        return locale;
    }

    public static void setLocale(Locale locale) {
        LocaleContext.locale = locale;
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(Locale defaultLocale) {
        LocaleContext.defaultLocale = defaultLocale;
    }
}
