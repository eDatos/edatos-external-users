package es.gobcan.istac.edatos.external.users.core.domain.vo;

import java.time.Instant;
import java.util.Date;

public class AuditEventVO {

    private Instant timestamp;
    private String  principal;
    private  String type;
    private  String data;

    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public String getPrincipal() {
        return principal;
    }
    public void setPrincipal(String principal) {
        this.principal = principal;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
