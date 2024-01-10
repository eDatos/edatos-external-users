package es.gobcan.istac.edatos.external.users.core.domain.enumeration;

public enum Language {
    SPANISH("es"),
    CATALAN("ca");

    private final String localeCode;

    Language(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public static Language findByLocaleCode(String localeCode) {
        for (Language language : values()) {
            if (language.localeCode.equals(localeCode)) {
                return language;
            }
        }
        return null;
    }
}
