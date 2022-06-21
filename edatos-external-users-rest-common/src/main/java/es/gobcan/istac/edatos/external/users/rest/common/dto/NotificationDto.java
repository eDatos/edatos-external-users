package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.util.List;

import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;

public class NotificationDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subject;
    private String message;
    private List<String> receivers;
    private List<Long> categoriesIds;
    private List<Long> externalOperationsIds;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public List<Long> getCategoriesIds() {
        return categoriesIds;
    }

    public void setCategoriesIds(List<Long> categoriesIds) {
        this.categoriesIds = categoriesIds;
    }

    public List<Long> getExternalOperationsIds() {
        return externalOperationsIds;
    }

    public void setExternalOperationsIds(List<Long> externalOperationsIds) {
        this.externalOperationsIds = externalOperationsIds;
    }
}
