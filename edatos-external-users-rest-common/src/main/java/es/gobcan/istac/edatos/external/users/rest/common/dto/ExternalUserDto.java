package es.gobcan.istac.edatos.external.users.rest.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Language;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Nationality;
import es.gobcan.istac.edatos.external.users.rest.common.dto.interfaces.AbstractVersionedAndAuditingAndLogicalDeletionDto;

public class ExternalUserDto extends AbstractVersionedAndAuditingAndLogicalDeletionDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String email;

    private String name;
    private String surname1;
    private String surname2;

    private Language language;
    private String phoneNumber;
    private String password;
    private String organism;
    private boolean emailNotificationsEnabled;

    private String nationality;

    private String gender;

    private LocalDate birthday;
    private DataProtectionPolicyDto dataProtectionPolicyDto;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getNationality() {
        return nationality;
    }
    public void setNationality(Nationality nationality) {
        this.nationality = nationality.getCodeM();
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public DataProtectionPolicyDto getDataProtectionPolicyDto() {
        return dataProtectionPolicyDto;
    }
    public void setDataProtectionPolicyDto(DataProtectionPolicyDto dataProtectionPolicyDto) {
        this.dataProtectionPolicyDto = dataProtectionPolicyDto;
    }
}
