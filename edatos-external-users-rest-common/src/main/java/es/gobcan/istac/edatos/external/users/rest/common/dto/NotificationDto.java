package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.util.List;

import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;

public class NotificationDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subject;
    private String message;

    private List<String> receivers;

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
}
