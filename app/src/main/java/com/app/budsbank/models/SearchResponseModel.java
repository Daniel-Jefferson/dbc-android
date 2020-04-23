package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResponseModel {
    @SerializedName("status")
    private String status;
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("message")
    private String message;
    @SerializedName("dispensaries")
    private ArrayList<DispensaryModel> dispensariesList;
    @SerializedName("pageInfo")
    private PageInfoModel pageInfo;

    public SearchResponseModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public ArrayList<DispensaryModel> getDispensariesList() {
        return dispensariesList;
    }

    public void setDispensariesList(ArrayList<DispensaryModel> dispensariesList) {
        this.dispensariesList = dispensariesList;
    }

    public PageInfoModel getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoModel pageInfo) {
        this.pageInfo = pageInfo;
    }
}
