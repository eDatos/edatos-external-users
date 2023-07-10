package es.gobcan.istac.edatos.external.users.core.domain.vo;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class InternationalStringVO implements Serializable {

    private final Set<LocalisedStringVO> texts = new HashSet<>();

    public Set<LocalisedStringVO> getTexts() {
        return texts;
    }

    public void addText(LocalisedStringVO textElement) {
        getTexts().add(textElement);
    }

    public void removeText(LocalisedStringVO textElement) {
        getTexts().remove(textElement);
    }

    public void removeAllTexts() {
        getTexts().clear();
    }

    public LocalisedStringVO getLocalisedLabelEntity(String locale, String defaultLocale) {
        if (locale == null) {
            return null;
        }
        LocalisedStringVO defaultLocaleEntity = null;
        for (LocalisedStringVO localstr : getTexts()) {
            if (locale.equalsIgnoreCase(localstr.getLocale()) && StringUtils.isNotBlank(localstr.getLabel())) {
                return localstr;
            } else if (defaultLocale != null && defaultLocale.equalsIgnoreCase(localstr.getLocale())) {
                defaultLocaleEntity = localstr;
            }
        }
        return defaultLocaleEntity;
    }

    public String getLocalisedLabel(String locale, String defaultLocale) {
        if (locale == null) {
            return null;
        }
        String defaultLocaleLabel = null;
        for (LocalisedStringVO localstr : getTexts()) {
            if (locale.equalsIgnoreCase(localstr.getLocale()) && StringUtils.isNotBlank(localstr.getLabel())) {
                return localstr.getLabel();
            } else if (defaultLocale != null && defaultLocale.equalsIgnoreCase(localstr.getLocale())) {
                defaultLocaleLabel = localstr.getLabel();
            }
        }
        return defaultLocaleLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternationalStringVO)) {
            return false;
        }
        InternationalStringVO that = (InternationalStringVO) o;
        return Objects.equals(texts, that.texts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texts);
    }

    @Override
    public String toString() {
        return texts.toString();
    }
}
