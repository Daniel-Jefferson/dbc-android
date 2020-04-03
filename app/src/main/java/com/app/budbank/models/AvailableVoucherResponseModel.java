package com.app.budbank.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AvailableVoucherResponseModel {
    @SerializedName("status")
    private int status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("vouchers")
    ArrayList<VoucherModel> vouchers;
    @SerializedName("pageInfo")
    private PageInfoModel pageInfo;

    public AvailableVoucherResponseModel() {
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

    public ArrayList<VoucherModel> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VoucherModel> vouchers) {
        this.vouchers = vouchers;
    }

    public PageInfoModel getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoModel pageInfo) {
        this.pageInfo = pageInfo;
    }
}
