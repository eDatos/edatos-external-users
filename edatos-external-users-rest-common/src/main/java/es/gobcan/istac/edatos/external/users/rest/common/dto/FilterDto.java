package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.time.Instant;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.Identifiable;

public class FilterDto extends AbstractVersionedAndAuditingDto implements Serializable, Identifiable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String resourceName;
    private ExternalUserDto externalUser;
    private String permalink;
    private String urlPermalink;
    private Instant lastAccessDate;
    private String notes;
    private ExternalOperationDto externalOperation;
    private PermalinkType permalinkType;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Instant getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Instant lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public ExternalUserDto getExternalUser() {
        return externalUser;
    }

    public void setExternalUser(ExternalUserDto externalUser) {
        this.externalUser = externalUser;
    }

    public ExternalOperationDto getExternalOperation() {
        return externalOperation;
    }

    public void setExternalOperation(ExternalOperationDto externalOperation) {
        this.externalOperation = externalOperation;
    }

    public String getUrlPermalink() {
        return urlPermalink;
    }

    public void setUrlPermalink(String urlPermalink) {
        this.urlPermalink = urlPermalink;
    }

    public PermalinkType getPermalinkType() {
        return permalinkType;
    }

    public void setPermalinkType(PermalinkType permalinkType) {
        this.permalinkType = permalinkType;
    }
}
