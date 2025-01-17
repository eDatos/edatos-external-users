package es.gobcan.istac.edatos.external.users.core.domain.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.siemac.metamac.rest.notices.v1_0.domain.Messages;

public class NotificationBaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String urn;
    private String subject;
    private Messages messages;
    private Date sendingDate;
    private List<String> receivers;

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }
}
