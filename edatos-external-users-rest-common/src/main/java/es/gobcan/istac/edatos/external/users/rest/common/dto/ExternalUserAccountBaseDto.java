package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Language;
import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.Identifiable;

public class ExternalUserAccountBaseDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable, Identifiable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;

    private String name;
    private String surname1;
    private String surname2;

    private Language language;
    private String phoneNumber;

    private String organism;
    private boolean emailNotificationsEnabled;

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

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public boolean isEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public void updateFrom(ExternalUserAccountBaseDto source) {
        this.id = source.getId();
        this.name = source.getName();
        this.surname1 = source.getSurname1();
        this.surname2 = source.getSurname2();
        this.email = source.getEmail();
        this.language = source.getLanguage();
        this.phoneNumber = source.getPhoneNumber();
        this.organism = source.getOrganism();
        this.emailNotificationsEnabled = source.isEmailNotificationsEnabled();
        this.setOptLock(source.getOptLock());
        this.setCreatedDate(source.getCreatedDate());
        this.setCreatedBy(source.getCreatedBy());
    }

    @Override
    public String toString() {
        return "ExternalUserAccountBaseDto [id=" + id + ", email=" + email + ", name=" + name + "]";
    }
}
