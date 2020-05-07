package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class SignupModel {

    @SerializedName("userName")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;

    public SignupModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
