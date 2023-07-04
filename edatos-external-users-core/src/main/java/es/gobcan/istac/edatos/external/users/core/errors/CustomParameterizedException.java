package es.gobcan.istac.edatos.external.users.core.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.springframework.context.MessageSource;

public class CustomParameterizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;
    private List<String> params = new ArrayList<>();
    private List<ParameterizedErrorItem> errorItems = new ArrayList<>();

    private Boolean translate = false;
    private Object[] translateArgs;

    protected CustomParameterizedException(String message) {
        super(message);
    }

    protected CustomParameterizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterizedErrorVM getParameterizedErrorVM() {
        return new ParameterizedErrorVM(getMessage(), code, params, errorItems);
    }

    public ParameterizedErrorVM getParameterizedErrorVM(MessageSource messageSource) {
        return new ParameterizedErrorVM(getTranslatedMessage(messageSource), code, params, errorItems);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public void addErrorItem(ParameterizedErrorItem errorItem) {
        this.errorItems.add(errorItem);
    }

    public Boolean getTranslate() {
        return translate;
    }

    public void setTranslate(Boolean translate) {
        this.translate = translate;
    }

    public Object[] getTranslateArgs() {
        return translateArgs;
    }

    public void setTranslateArgs(Object[] translateArgs) {
        this.translateArgs = translateArgs;
    }

    private String getTranslatedMessage(MessageSource messageSource) {
        Locale currentLocale = LocaleContext.getLocale();
        return messageSource.getMessage(code, translateArgs, currentLocale);
    }
}
