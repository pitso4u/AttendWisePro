package com.attendwisepro.models;

import com.google.gson.annotations.SerializedName;

public class CountResponse {
    @SerializedName("count")
    private int count;

    public CountResponse(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
