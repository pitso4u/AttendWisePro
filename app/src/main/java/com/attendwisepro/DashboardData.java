package com.attendwisepro;

import com.google.gson.annotations.SerializedName;

public class DashboardData {
    @SerializedName("present_count")
    private int presentCount;

    @SerializedName("absent_count")
    private int absentCount;

    @SerializedName("recent_activity")
    private RecentActivity[] recentActivity;

    public static class RecentActivity {
        @SerializedName("type")
        private String type;

        @SerializedName("description")
        private String description;

        @SerializedName("timestamp")
        private String timestamp;

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public int getPresentCount() {
        return presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public RecentActivity[] getRecentActivity() {
        return recentActivity;
    }
}
