package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class SettingsModel {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("dispensary_id")
    private String dispensaryId;
    @SerializedName("enable")
    private String isEnabled;

    public SettingsModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDispensaryId() {
        return dispensaryId;
    }

    public void setDispensaryId(String dispensaryId) {
        this.dispensaryId = dispensaryId;
    }

    public String isEnabled() {
        return isEnabled;
    }

    public void setEnabled(String enabled) {
        isEnabled = enabled;
    }
}
