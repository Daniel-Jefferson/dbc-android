package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TimeDataModel implements Serializable {
    @SerializedName("weekday")
    private int weekday;
    @SerializedName("open_time")
    private String openTime;
    @SerializedName("close_time")
    private String closeTime;


    public int getWeekday() {
        return weekday;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }
}
