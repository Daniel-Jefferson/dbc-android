package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class ForgetPasswordModel {

    @SerializedName("phone")
    private String phone;

    public ForgetPasswordModel(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
