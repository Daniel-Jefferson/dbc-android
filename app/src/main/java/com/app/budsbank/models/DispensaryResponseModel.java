package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class DispensaryResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("dispensary")
    private  DispensaryModel dispensaryModel;

    public DispensaryResponseModel() {
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

    public DispensaryModel getDispensaryModel() {
        return dispensaryModel;
    }

    public void setDispensaryModel(DispensaryModel dispensaryModel) {
        this.dispensaryModel = dispensaryModel;
    }
}
