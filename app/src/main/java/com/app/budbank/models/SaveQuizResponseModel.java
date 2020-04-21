package com.app.budbank.models;

public class SaveQuizResponseModel extends ResponseModel {
    private VoucherModel voucher;
    private int remainCount;

    public VoucherModel getVoucher() {
        return voucher;
    }

    public int getRemainCount() {
        return remainCount;
    }
}
