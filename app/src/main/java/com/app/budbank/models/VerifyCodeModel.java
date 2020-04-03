package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

public class VerifyCodeModel {
    @SerializedName("x-access-token")
    private String sessionToken;
    @SerializedName("verificationCode")
    private String verificationCode;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("latitude")
    private double latitude;

    public VerifyCodeModel() {
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
