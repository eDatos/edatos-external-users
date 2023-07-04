package es.gobcan.istac.edatos.external.users.web.security.provider;

import es.gobcan.istac.edatos.external.users.core.errors.ParameterizedErrorVM;
import es.gobcan.istac.edatos.external.users.core.i18n.LocaleContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;
import java.util.Locale;

public class LoginAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -2075386713733803031L;

    private String code;
    private HttpStatus status;

    private Boolean translate = false;
    private Object[] translateArgs;

    public LoginAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public LoginAuthenticationException(String msg) {
        super(msg);
    }

    public ParameterizedErrorVM getParameterizedErrorVM() {
        return new ParameterizedErrorVM(getMessage(), code, Arrays.asList(), Arrays.asList());
    }

    public ParameterizedErrorVM getParameterizedErrorVM(MessageSource messageSource) {
        return new ParameterizedErrorVM(getTranslatedMessage(messageSource), code, Arrays.asList(), Arrays.asList());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
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
