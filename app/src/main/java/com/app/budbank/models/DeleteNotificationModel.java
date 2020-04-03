package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

public class DeleteNotificationModel {
    @SerializedName("notification_id")
    private int notificationId;
    @SerializedName("user_id")
    private String userId;

    public DeleteNotificationModel() {
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
