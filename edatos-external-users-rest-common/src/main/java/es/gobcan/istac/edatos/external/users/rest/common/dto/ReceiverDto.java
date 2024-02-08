package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;

public class ReceiverDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            email;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
