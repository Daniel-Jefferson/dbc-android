package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LoginResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private String isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private UserModel user;
    @SerializedName("User")
    private UserModel userModel;
    @SerializedName("dispensaries")
    private ArrayList<DispensaryModel> dispensaryList;
    @SerializedName("completed_dispensaries")
    private ArrayList<DispensaryModel> completedDispensariesList;
    @SerializedName("featured_dispensaries")
    private ArrayList<DispensaryModel> featuredDispensariesList;
    @SerializedName("available_vouchers")
    private ArrayList<VoucherModel> availableVouchersList;
    @SerializedName("redeemed_vouchers")
    private ArrayList<VoucherModel> redeemedVouchersList;

    public LoginResponseModel(int status, String isSuccess, String message, UserModel user) {
        this.status = status;
        this.isSuccess = isSuccess;
        this.message = message;
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserModel getUser() {
        return userModel != null ? userModel : user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ArrayList<DispensaryModel> getDispensaryList() {
        return dispensaryList;
    }

    public ArrayList<DispensaryModel> getCompletedDispensariesList() {
        return completedDispensariesList;
    }

    public ArrayList<VoucherModel> getAvailableVouchersList() {
        return availableVouchersList;
    }

    public ArrayList<VoucherModel> getRedeemedVouchersList() {
        return redeemedVouchersList;
    }

    public ArrayList<DispensaryModel> getFeaturedDispensariesList() {
        return featuredDispensariesList;
    }
}
