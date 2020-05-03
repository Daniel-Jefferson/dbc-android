package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class UpdateUserModel {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("username")
    private String userName;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("image")
    private String profileUrl;

    public UpdateUserModel() {
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
