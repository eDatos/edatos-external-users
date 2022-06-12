package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.util.List;

import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;

public class NotificationDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subject;
    private String message;

    private List<String> receivers;

    private List<Long   > categories;
    private List<Long> externalOperations;

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

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<Long> getExternalOperations() {
        return externalOperations;
    }

    public void setExternalOperations(List<Long> externalOperations) {
        this.externalOperations = externalOperations;
    }
}
