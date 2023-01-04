package es.gobcan.istac.edatos.external.users.core.service.validator;

import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginValidator extends AbstractValidator<String> {

    @Autowired
    private ExternalUserRepository externalUserRepository;

    public void validate(String login) {
        if (StringUtils.isNotBlank(login)) {
            checkExistsUsuarioWithLogin(login);
            checkUsuarioIsActivated(login);
        } else {
            throw new EDatosException(ServiceExceptionType.LOGIN_VOID);
        }
    }

    private void checkExistsUsuarioWithLogin(String login) {
        if (!externalUserRepository.existsByEmailIgnoreCase(login)) {
            throw new EDatosException(ServiceExceptionType.LOGIN_FAILED);
        }
    }

    private void checkUsuarioIsActivated(String login) {
        if (!externalUserRepository.existsByEmailIgnoreCase(login)) {
            throw new EDatosException(ServiceExceptionType.LOGIN_USER_DISABLED);
        }
    }

    public void checkUserAuthenticated(String owner) {
        String userAuthenticated = SecurityUtils.getCurrentUserLogin();
        if (!userAuthenticated.equalsIgnoreCase(owner)) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_DEAUTHENTICATED);
        }
    }

}
