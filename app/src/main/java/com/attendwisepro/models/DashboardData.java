package com.attendwisepro.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DashboardData {
    @SerializedName("total_learners")
    private int totalLearners;

    @SerializedName("total_employees")
    private int totalEmployees;

    @SerializedName("learners_absent")
    private int absentLearners;

    @SerializedName("employees_absent")
    private int absentEmployees;

    @SerializedName("recent_activities")
    private List<RecentActivity> recentActivities;

    public DashboardData(int totalLearners, int totalEmployees, 
                        int absentLearners, int absentEmployees,
                        List<RecentActivity> recentActivities) {
        this.totalLearners = totalLearners;
        this.totalEmployees = totalEmployees;
        this.absentLearners = absentLearners;
        this.absentEmployees = absentEmployees;
        this.recentActivities = recentActivities;
    }

    public int getTotalLearners() {
        return totalLearners;
    }

    public void setTotalLearners(int totalLearners) {
        this.totalLearners = totalLearners;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getAbsentLearners() {
        return absentLearners;
    }

    public void setAbsentLearners(int absentLearners) {
        this.absentLearners = absentLearners;
    }

    public int getAbsentEmployees() {
        return absentEmployees;
    }

    public void setAbsentEmployees(int absentEmployees) {
        this.absentEmployees = absentEmployees;
    }

    public List<RecentActivity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivity> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
