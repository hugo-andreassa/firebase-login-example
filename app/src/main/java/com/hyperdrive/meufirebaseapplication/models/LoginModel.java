package com.hyperdrive.meufirebaseapplication.models;

public class LoginModel {

    private String email;
    private String telefone;
    private String password;

    public LoginModel() {
    }

    public LoginModel(String email, String telefone, String password) {
        this.email = email;
        this.telefone = telefone;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
