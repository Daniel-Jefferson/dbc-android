package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetNotificationResponseModel implements Serializable {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("notifications")
    private Notifications notifications;

    public GetNotificationResponseModel() {
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

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }
}
