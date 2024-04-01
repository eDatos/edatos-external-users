package es.gobcan.istac.edatos.external.users.core.config.audit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.Constants;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;

@Component
public class AuditEventPublisher implements ApplicationEventPublisherAware {

    @Autowired
    private ExternalUserService externalUserService;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String type, AuditPrincipalType principalType, String identifier, String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(AuditConstants.CODE, identifier);
        map.put(AuditConstants.MESSAGE, message);
        map.put(AuditConstants.PRINCIPAL_TYPE, principalType);
        publish(type, map, principalType);
    }

    public void publish(String type, AuditPrincipalType principalType, String identifier) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(AuditConstants.CODE, identifier);
        map.put(AuditConstants.PRINCIPAL_TYPE, principalType);
        publish(type, map, principalType);
    }

    public void publish(String type, AuditPrincipalType principalType) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(AuditConstants.PRINCIPAL_TYPE, principalType);
        publish(type, map, principalType);
    }

    private void publish(String type, Map<String, Object> map, AuditPrincipalType principalType) {
        publish(createAuditEvent(type, map, principalType));
    }

    private void publish(AuditEvent event) {
        if (this.publisher != null) {
            this.publisher.publishEvent(new AuditApplicationEvent(event));
        }
    }

    private AuditEvent createAuditEvent(String type, Map<String, Object> map, AuditPrincipalType principalType) {
        String userName = SecurityUtils.getCurrentUserLogin();
        if (userName == null) {
            return new AuditEvent(Constants.SYSTEM_ACCOUNT, type, map);
        } else if (AuditPrincipalType.EXTERNAL_USER.equals(principalType)) {
            ExternalUserEntity externalUser = externalUserService.findByEmail(userName).get();
            return new AuditEvent(externalUser.getId().toString(), type, map);
        } else {
            return new AuditEvent(userName, type, map);
        }
    }
}
