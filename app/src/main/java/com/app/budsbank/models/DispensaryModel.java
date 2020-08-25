package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DispensaryModel implements Serializable {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String phone;
    private String address;
    @SerializedName("image")
    private String profileUrl;
    private String created;
    @SerializedName("is_followed")
    private boolean isFollowed;
    @SerializedName("deal")
    private String deal;
    @SerializedName("open_close_time")
    private OpenCloseTimeModel openCloseTimeModel;
    @SerializedName("is_available")
    private boolean isAvailable;
    @SerializedName("products")
    private ArrayList<ProductModel> productModels;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public ArrayList<ProductModel> getProductModels() { return productModels; }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getCreated() {
        return created;
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public void setProductModels(ArrayList<ProductModel> productModels) { this.productModels = productModels;}

    public OpenCloseTimeModel getOpenCloseTimeModel() {
        return openCloseTimeModel;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
