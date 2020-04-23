package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class ForgetPasswordModel {

    @SerializedName("email")
    private String email;

    public ForgetPasswordModel(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
