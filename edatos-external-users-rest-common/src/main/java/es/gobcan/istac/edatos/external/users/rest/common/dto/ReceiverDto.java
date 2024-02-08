package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;

public class ReceiverDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String receiver;

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
