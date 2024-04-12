package es.gobcan.istac.edatos.external.users.core.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.domain.AuditEventEntity;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventConverter;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;

@Repository
public class CustomAuditEventRepository implements AuditEventRepository {

    private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    private final AuditEventConverter auditEventConverter;

    public CustomAuditEventRepository(PersistenceAuditEventRepository persistenceAuditEventRepository, AuditEventConverter auditEventConverter) {

        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }

    @Override
    public List<AuditEvent> find(Date after) {
        Iterable<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findByAuditEventDateAfter(after.toInstant());
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    @Override
    public List<AuditEvent> find(String principal, Date after) {
        Iterable<AuditEventEntity> persistentAuditEvents;
        if (principal == null && after == null) {
            persistentAuditEvents = persistenceAuditEventRepository.findAll();
        } else if (after == null) {
            persistentAuditEvents = persistenceAuditEventRepository.findByPrincipal(principal);
        } else {
            persistentAuditEvents = persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfter(principal, after.toInstant());
        }
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    @Override
    public List<AuditEvent> find(String principal, Date after, String type) {
        Iterable<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, after.toInstant(), type);
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {
        if (!AUTHORIZATION_FAILURE.equals(event.getType())) {
            HashMap<String, Object> data = new HashMap<>(event.getData());
            AuditPrincipalType principalType = (AuditPrincipalType) data.remove(AuditConstants.PRINCIPAL_TYPE);

            AuditEventEntity persistentAuditEvent = new AuditEventEntity();
            persistentAuditEvent.setPrincipal(event.getPrincipal());
            persistentAuditEvent.setPrincipalType(principalType == null ? AuditPrincipalType.ADMIN : principalType);
            persistentAuditEvent.setAuditEventType(event.getType());
            persistentAuditEvent.setAuditEventDate(event.getTimestamp().toInstant());
            persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(data));
            persistenceAuditEventRepository.saveAndFlush(persistentAuditEvent);
        }
    }
}
