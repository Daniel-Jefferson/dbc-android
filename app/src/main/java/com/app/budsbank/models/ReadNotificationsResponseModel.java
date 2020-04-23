package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReadNotificationsResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("notifications")
    private ArrayList<NotificationModel> notifications;
    @SerializedName("pageInfo")
    private PageInfoModel pageInfo;

    public ReadNotificationsResponseModel() {
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

    public ArrayList<NotificationModel> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationModel> notifications) {
        this.notifications = notifications;
    }

    public PageInfoModel getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoModel pageInfo) {
        this.pageInfo = pageInfo;
    }
}
