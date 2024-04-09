package es.gobcan.istac.edatos.external.users.core.config.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.domain.AuditEventEntity;

import static es.gobcan.istac.edatos.external.users.core.config.AuditConstants.PRINCIPAL_TYPE;

@Component
public class AuditEventConverter {

    /**
     * Convert a list of PersistentAuditEvent to a list of AuditEvent
     *
     * @param persistentAuditEvents
     *            the list to convert
     * @return the converted list.
     */
    public List<AuditEvent> convertToAuditEvent(Iterable<AuditEventEntity> auditEventsEntity) {
        if (auditEventsEntity == null) {
            return Collections.emptyList();
        }
        List<AuditEvent> auditEvents = new ArrayList<>();
        for (AuditEventEntity auditEvent : auditEventsEntity) {
            auditEvents.add(convertToAuditEvent(auditEvent));
        }
        return auditEvents;
    }

    /**
     * Convert a PersistentAuditEvent to an AuditEvent
     *
     * @param auditEvent
     *            the event to convert
     * @return the converted list.
     */
    public AuditEvent convertToAuditEvent(AuditEventEntity auditEvent) {
        if (auditEvent == null) {
            return null;
        }
        Map<String, Object> data = convertDataToObjects(auditEvent.getData(), auditEvent.getPrincipalType());
        return new AuditEvent(Date.from(auditEvent.getAuditEventDate()), auditEvent.getPrincipal(), auditEvent.getAuditEventType(), data);
    }

    /**
     * Internal conversion. This is needed to support the current SpringBoot
     * actuator AuditEventRepository interface
     *
     * @param data
     *            the data to convert
     * @return a map of String, Object
     */
    public Map<String, Object> convertDataToObjects(Map<String, String> data, AuditPrincipalType principalType) {
        Map<String, Object> results = new HashMap<>();
        results.put(PRINCIPAL_TYPE, principalType);
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }

    /**
     * Internal conversion. This method will allow to save additional data. By
     * default, it will save the object as string
     *
     * @param data
     *            the data to convert
     * @return a map of String, String
     */
    public Map<String, String> convertDataToStrings(Map<String, Object> data) {
        Map<String, String> results = new HashMap<>();

        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Object object = entry.getValue();

                // Extract the data that will be saved.
                if (object instanceof WebAuthenticationDetails) {
                    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) object;
                    results.put("remoteAddress", authenticationDetails.getRemoteAddress());
                    results.put("sessionId", authenticationDetails.getSessionId());
                } else if (object != null) {
                    results.put(entry.getKey(), object.toString());
                } else {
                    results.put(entry.getKey(), "null");
                }
            }
        }

        return results;
    }
}
