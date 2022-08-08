package es.gobcan.istac.edatos.external.users.core.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;

import es.gobcan.istac.edatos.external.users.core.domain.interfaces.AbstractVersionedAndAuditingEntity;

/**
 * Filters are visualizations the user saved to consult them later. They work through permalinks, that contains
 * information about the visualization (i.e. the selected dimensions and categories). This entity saves extra info that
 * can be obtained though permalinks to save time and avoid server overload.
 */
@Entity
@Table(name = "tb_filters", uniqueConstraints = {@UniqueConstraint(columnNames = {"external_user_fk", "permalink"})})
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class FilterEntity extends AbstractVersionedAndAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_filters")
    @SequenceGenerator(name = "seq_tb_filters", sequenceName = "seq_tb_filters", allocationSize = 50, initialValue = 1)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 1)
    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "external_user_fk", nullable = false)
    private ExternalUserEntity externalUser;

    @NotNull
    @Column(nullable = false)
    private String permalink;

    @NotNull
    @Column(nullable = false)
    private Instant lastAccessDate = Instant.now();

    @Column(length = 4000)
    private String notes;

    @ManyToOne(targetEntity = ExternalItemEntity.class)
    @JoinColumn(columnDefinition = "external_operation_fk", referencedColumnName = "urn")
    private ExternalOperationEntity externalOperation;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "permalink_type default VISUALIZER", nullable = false)
    private PermalinkType permalinkType;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExternalUserEntity getExternalUser() {
        return externalUser;
    }

    public void setExternalUser(ExternalUserEntity user) {
        this.externalUser = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Instant lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public ExternalOperationEntity getExternalOperation() {
        return externalOperation;
    }

    public void setExternalOperation(ExternalOperationEntity externalOperation) {
        this.externalOperation = externalOperation;
    }

    public PermalinkType getPermalinkType() {
        return permalinkType;
    }

    public void setPermalinkType(PermalinkType permalinkType) {
        this.permalinkType = permalinkType;
    }
}
