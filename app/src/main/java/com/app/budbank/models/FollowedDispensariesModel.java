package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

public class FollowedDispensariesModel {
    @SerializedName("id")
    private int dispensaryId;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String profileUrl;
    @SerializedName("enable")
    private boolean isEnabled;
    @SerializedName("deal")
    private String deal;

    public FollowedDispensariesModel() {
    }

    public int getDispensaryId() {
        return dispensaryId;
    }

    public void setdispensaryId(int dispensaryId) {
        this.dispensaryId = dispensaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }
}
