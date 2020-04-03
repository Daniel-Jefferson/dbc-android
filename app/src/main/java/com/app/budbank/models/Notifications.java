package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Notifications implements Serializable {
    @SerializedName("seen_notifications")
    private ArrayList<NotificationModel> seenNotifications;
    @SerializedName("unseen_notifications")
    private ArrayList<NotificationModel> unseenNotifications;

    public Notifications() {
    }

    public ArrayList<NotificationModel> getSeenNotifications() {
        return seenNotifications;
    }

    public void setSeenNotifications(ArrayList<NotificationModel> seenNotifications) {
        this.seenNotifications = seenNotifications;
    }

    public ArrayList<NotificationModel> getUnseenNotifications() {
        return unseenNotifications;
    }

    public void setUnseenNotifications(ArrayList<NotificationModel> unseenNotifications) {
        this.unseenNotifications = unseenNotifications;
    }
}
