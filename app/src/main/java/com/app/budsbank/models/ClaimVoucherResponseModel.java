package com.app.budsbank.models;

import java.util.ArrayList;

public class ClaimVoucherResponseModel extends ResponseModel {
    private ArrayList<VoucherModel> voucher;

    public ArrayList<VoucherModel> getVoucherList() {
        return voucher;
    }

    public VoucherModel getVoucher() {
        return voucher != null && voucher.size() > 0 ? voucher.get(0) : null;
    }
}
