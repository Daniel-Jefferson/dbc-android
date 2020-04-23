package com.app.budsbank.models;

public class BottomSheetModel {
    private String data;
    private int resourceId;
    private String hostId;

    public BottomSheetModel(String data) {
        this.data = data;
    }

    public BottomSheetModel(String data, int resourceId) {

        this.data = data;
        this.resourceId = resourceId;
    }

    public BottomSheetModel(String data, int resourceId, String hostId) {
        this.data = data;
        this.resourceId = resourceId;
        this.hostId = hostId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}


