package com.app.budsbank.models;

import java.io.Serializable;

public class VoucherModel implements Serializable {
    private int voucher_id;
    private int dispensary_id;
    private String dispensary_name;
    private String dispensary_address;
    private String expiry;
    private String isAvailable;
    private String isRedeemed;
    private String reward;

    public VoucherModel() {
    }

    public VoucherModel(int voucher_id, int dispensary_id, String dispensary_name, String dispensary_address, String expiry, String isAvailable, String isRedeemed, String reward) {
        this.voucher_id = voucher_id;
        this.dispensary_id = dispensary_id;
        this.dispensary_name = dispensary_name;
        this.dispensary_address = dispensary_address;
        this.expiry = expiry;
        this.isAvailable = isAvailable;
        this.isRedeemed = isRedeemed;
        this.reward = reward;
    }

    public int getVoucherId() {
        return voucher_id;
    }

    public void setVoucherId(int voucher_id) {
        this.voucher_id = voucher_id;
    }

    public int getDispensaryId() {
        return dispensary_id;
    }

    public void setDispensaryId(int dispensary_id) {
        this.dispensary_id = dispensary_id;
    }

    public String getDispensaryName() {
        return dispensary_name;
    }

    public void setDispensaryName(String dispensary_name) {
        this.dispensary_name = dispensary_name;
    }

    public String getDispensaryAddress() {
        return dispensary_address;
    }

    public void setDispensaryAddress(String dispensary_address) {
        this.dispensary_address = dispensary_address;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIsRedeemed() {
        return isRedeemed;
    }

    public void setIsRedeemed(String isRedeemed) {
        this.isRedeemed = isRedeemed;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public VoucherModel(VoucherModel voucherModel) {
        this.voucher_id = voucherModel.voucher_id;
        this.dispensary_id = voucherModel.dispensary_id;
        this.dispensary_name = voucherModel.dispensary_name;
        this.dispensary_address = voucherModel.dispensary_address;
        this.expiry = voucherModel.expiry;
        this.isAvailable = voucherModel.isAvailable;
        this.isRedeemed = voucherModel.isRedeemed;
        this.reward = voucherModel.reward;
    }
}
