package es.gobcan.istac.edatos.external.users.core.domain.vo;

import java.time.Instant;

public class ExternalUserVO {

    private String name;
    private String surname1;
    private String surname2;
    private String  birthday;
    private  String nationality;
    private  String gender;
    private  String organism;
    private  String phoneNumber;
    private  String email;

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
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getOrganism() {
        return organism;
    }
    public void setOrganism(String organism) {
        this.organism = organism;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
