package com.app.budsbank.models.requestModel;

import com.google.gson.annotations.SerializedName;

public class FollowUnFollowRequestModel {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("dispensary_id")
    private int dispensaryId;

    public FollowUnFollowRequestModel(String userId, int dispensaryId) {
        this.userId = userId;
        this.dispensaryId = dispensaryId;
    }
}
