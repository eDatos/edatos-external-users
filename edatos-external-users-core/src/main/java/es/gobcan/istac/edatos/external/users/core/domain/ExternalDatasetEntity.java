package es.gobcan.istac.edatos.external.users.core.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.siemac.edatos.core.common.enume.TypeExternalArtefactsEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tb_external_datasets")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name = "external_item_fk")
@EntityListeners(AuditingEntityListener.class)
public class ExternalDatasetEntity extends ExternalItemEntity {

    private static final long serialVersionUID = -3223278152050381860L;
    @NotNull
    @NotBlank
    @Column(nullable = false)
    private String externalOperationUrn;

    @CreatedDate
    @NotNull
    @Column(name = "recivedDate", nullable = false, updatable = false)
    private Instant createdDate;

    @NotNull
    @Column(nullable = false)
    @URL
    private String visualizerHtmlLink;

    public ExternalDatasetEntity() {
        type = TypeExternalArtefactsEnum.DATASET;
    }

    public String getExternalOperationUrn() {
        return externalOperationUrn;
    }

    public void setExternalOperationUrn(String externalOperationUrn) {
        this.externalOperationUrn = externalOperationUrn;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getVisualizerHtmlLink() {
        return visualizerHtmlLink;
    }

    public void setVisualizerHtmlLink(String visualizerHtmlLink) {
        this.visualizerHtmlLink = visualizerHtmlLink;
    }
}
