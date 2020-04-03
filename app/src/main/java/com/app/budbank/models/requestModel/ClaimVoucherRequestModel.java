package com.app.budbank.models.requestModel;

import com.google.gson.annotations.SerializedName;

public class ClaimVoucherRequestModel {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("voucher_id")
    private int voucherId;
    @SerializedName("code")
    private String code;

    public ClaimVoucherRequestModel(String userId, int voucherId, String code) {
        this.userId = userId;
        this.voucherId = voucherId;
        this.code = code;
    }
}
