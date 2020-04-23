package com.app.budsbank.models.requestModel;

import com.google.gson.annotations.SerializedName;

public class SaveQuizRequestModel {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("dispensary_id")
    private int dispensaryId;
    private String success;

    public SaveQuizRequestModel(String userId, int dispensaryId, String isSuccess) {
        this.userId = userId;
        this.dispensaryId = dispensaryId;
        this.success = isSuccess;
    }

    public String getUserId() {
        return userId;
    }

    public int getDispensaryId() {
        return dispensaryId;
    }

    public String getIsSuccess() {
        return success;
    }
}
