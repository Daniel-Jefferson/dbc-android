package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

public class PageInfoModel {
    @SerializedName("currentPage")
    private int currentPage;
    @SerializedName("pageCount")
    private int pageCount;
    @SerializedName("pageSize")
    private int pageSize;
    @SerializedName("count")
    private int count;

    public PageInfoModel() {
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
