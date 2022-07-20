package es.gobcan.istac.edatos.external.users.rest.common.dto;

import org.siemac.edatos.core.common.exception.EDatosException;

public class ResponseWithNotificationException<T> {

    private T bodyContent;
    private EDatosException notificationException;

    public T getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(T bodyContent) {
        this.bodyContent = bodyContent;
    }

    public EDatosException getNotificationException() {
        return notificationException;
    }

    public void setNotificationException(EDatosException notificationException) {
        this.notificationException = notificationException;
    }
}
