package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpenCloseTimeModel implements Serializable {
    @SerializedName("open_day")
    private String openDay;
    @SerializedName("close_day")
    private String closeDay;
    @SerializedName("open_time")
    private String openTime;
    @SerializedName("close_time")
    private String closeTime;

    public String getOpenDay() {
        return openDay;
    }

    public String getCloseDay() {
        return closeDay;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
}
