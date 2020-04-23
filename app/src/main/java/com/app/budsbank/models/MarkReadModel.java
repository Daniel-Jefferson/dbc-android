package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class MarkReadModel {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("notification_id")
    private int notificationId;

    public MarkReadModel() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
