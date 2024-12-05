package com.attendwisepro.models;

import com.google.gson.annotations.SerializedName;

public class RecentActivity {
    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("timestamp")
    private String timestamp;

    public RecentActivity(String type, String description, String timestamp) {
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
