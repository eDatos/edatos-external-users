package es.gobcan.istac.edatos.external.users.core.service;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditEventService {

    Page<AuditEvent> find(Pageable pageable);

    List<AuditEvent> findByPrincipalAndPrincipalType(String principal, AuditPrincipalType principalType);

    Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable);

    Optional<AuditEvent> get(Long id);
}
