package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

public class UpdateUserResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSucces")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private UserModel user;

    public UpdateUserResponseModel() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
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
