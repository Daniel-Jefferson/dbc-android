package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductModel implements Serializable {
    @SerializedName("product_name")
    private String productName;
    @SerializedName("product_image")
    private String productImg;

    public ProductModel(String productName) {
        this.productName = productName;
    }

    public ProductModel(String data, String productImg) {
        this.productName = data;
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productImg) {
        this.productImg = productImg;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }
}


