package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.util.List;
import org.siemac.edatos.core.common.dto.InternationalStringDto;

import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;

public class NotificationDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private InternationalStringDto subject;
    private InternationalStringDto message;
    private List<String> receivers;
    private List<Long> categoriesIds;
    private List<Long> externalOperationsIds;
    private List<Long> filtersExternalOperationsIds;

    private List<String> failedReceivers;

    public InternationalStringDto getSubject() {
        return subject;
    }

    public void setSubject(InternationalStringDto subject) {
        this.subject = subject;
    }

    public InternationalStringDto getMessage() {
        return message;
    }

    public void setMessage(InternationalStringDto message) {
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

    public List<String> getFailedReceivers() {
        return failedReceivers;
    }
    public void setFailedReceivers(List<String> failedReceivers) {
        this.failedReceivers = failedReceivers;
    }

    public List<Long> getFiltersExternalOperationsIds() {
        return filtersExternalOperationsIds;
    }

    public void setFiltersExternalOperationsIds(List<Long> filtersExternalOperationsIds) {
        this.filtersExternalOperationsIds = filtersExternalOperationsIds;
    }
}
