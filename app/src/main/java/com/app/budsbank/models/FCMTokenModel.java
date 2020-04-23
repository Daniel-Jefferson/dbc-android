package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class FCMTokenModel {
    @SerializedName("token")
    private String token;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("device_id")
    private int deviceId;

    public FCMTokenModel() {
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
