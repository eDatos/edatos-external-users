package es.gobcan.istac.edatos.external.users.core.domain;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "admin_audit_event")
public class AuditEventEntity implements Serializable {

    private static final long serialVersionUID = 4766696832281719292L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_admin_audit_event")
    @SequenceGenerator(name = "seq_admin_audit_event", sequenceName = "seq_admin_audit_event", allocationSize = 50, initialValue = 1)
    @Column(name = "event_id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String principal;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "principal_type", nullable = false)
    private AuditPrincipalType principalType;

    @Column(name = "event_date")
    private Instant auditEventDate;
    @Column(name = "event_type")
    private String auditEventType;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "admin_audit_event_data", joinColumns = @JoinColumn(name = "event_id"))
    private Map<String, String> data = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public AuditPrincipalType getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(AuditPrincipalType principalType) {
        this.principalType = principalType;
    }

    public Instant getAuditEventDate() {
        return auditEventDate;
    }

    public void setAuditEventDate(Instant auditEventDate) {
        this.auditEventDate = auditEventDate;
    }

    public String getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(String auditEventType) {
        this.auditEventType = auditEventType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
