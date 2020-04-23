package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationSettingsResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("settings")
    private ArrayList<FollowedDispensariesModel> settings;
    @SerializedName("page_info")
    private PageInfoModel pageInfo;

    public NotificationSettingsResponseModel() {
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

    public ArrayList<FollowedDispensariesModel> getSettings() {
        return settings;
    }

    public void setSettings(ArrayList<FollowedDispensariesModel> settings) {
        this.settings = settings;
    }

    public PageInfoModel getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoModel pageInfo) {
        this.pageInfo = pageInfo;
    }
}
