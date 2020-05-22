package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OpenCloseTimeModel implements Serializable {
    @SerializedName("open_day")
    private String openDay;
    @SerializedName("close_day")
    private String closeDay;
    @SerializedName("time_data")
    private ArrayList<TimeDataModel> timeDataModelArray;

    public String getOpenDay() {
        return openDay;
    }

    public String getCloseDay() {
        return closeDay;
    }

    public ArrayList<TimeDataModel> getTimeDataModelArray() {
        return timeDataModelArray;
    }
}
