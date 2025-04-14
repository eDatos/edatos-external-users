package es.gobcan.istac.edatos.external.users.core.service.validator;

import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.service.MetadataConfigurationService;
import java.time.Instant;

@Component
public class ExternalUserValidator extends AbstractValidator<ExternalUserEntity> {

    @Autowired
    ExternalUserRepository externalUserRepository;

    @Autowired
    MetadataConfigurationService configurationService;

    public void checkRegistrationTokenHasNotExpired(ExternalUserEntity user) {
        if(user == null || user.getCreatedDate().isBefore(Instant.now().minusSeconds(86400))) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_REGISTRATION_CONFIRMATION);
        }
    }

    public void checkNameAndSurnameArePresentIfMandatory(ExternalUserEntity user) {
        if (!configurationService.retrieveSimplifiedSignUp() && (user.getName() == null || user.getSurname1() == null)) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_NAME_IS_NULL);
        }
    }
}
