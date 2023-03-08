package es.gobcan.istac.edatos.external.users.core.service;

import org.siemac.edatos.core.common.conf.ConfigurationService;
import org.siemac.edatos.core.common.exception.EDatosException;

public interface MetadataConfigurationService extends ConfigurationService {

    boolean retrieveCaptchaEnable() throws EDatosException;

    String retrieveCaptchaProvider() throws EDatosException;

    String retrieveRecaptchaSiteKey() throws EDatosException;

    String retrieveRecaptchaSecretKey() throws EDatosException;

    String retrieveBaseUrl() throws EDatosException;

    String retrieveCronExpressionSendNoticeJob() throws EDatosException;
    String retrieveNationalityUrnKey() throws EDatosException;

    String retrieveGenderUrnKey() throws EDatosException;
}
