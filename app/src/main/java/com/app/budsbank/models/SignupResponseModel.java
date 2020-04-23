package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class SignupResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private String isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private UserModel user;

    public SignupResponseModel(int status, String isSuccess, String message, UserModel user) {
        this.status = status;
        this.isSuccess = isSuccess;
        this.message = message;
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
