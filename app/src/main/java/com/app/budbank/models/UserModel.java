package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("session_token")
    private String sessionToken;
    @SerializedName("id")
    private String userId;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("email_verified_at")
    private String emailVerifiedAt;
    @SerializedName("username")
    private String Username;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("image")
    private String profileUrl;
    @SerializedName("coins_earned")
    private String coinsEarned;

    public UserModel() {
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(String coinsEarned) {
        this.coinsEarned = coinsEarned;
    }
}
