package com.hyperdrive.meufirebaseapplication.models;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String birthdayMonth;
    private String street;
    private String city;
    private String number;
    private String comp;
    private String cep;
    private String geographicPoint;

    public UserModel() {
    }

    public UserModel(String id, String name, String email, String password, String phone,
                     String birthdayMonth, String street, String city,
                     String number, String comp, String cep, String geographicPoint) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthdayMonth = birthdayMonth;
        this.street = street;
        this.city = city;
        this.number = number;
        this.comp = comp;
        this.cep = cep;
        this.geographicPoint = geographicPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdayMonth() {
        return birthdayMonth;
    }

    public void setBirthdayMonth(String birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComp() {
        return comp;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getGeographicPoint() {
        return geographicPoint;
    }

    public void setGeographicPoint(String geographicPoint) {
        this.geographicPoint = geographicPoint;
    }
}
