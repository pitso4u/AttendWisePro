package com.attendwisepro.models;

public class AttendanceDetail {
    private String date;
    private int present;
    private int absent;
    private float percentage;

    public AttendanceDetail(String date, int present, int absent) {
        this.date = date;
        this.present = present;
        this.absent = absent;
        this.percentage = calculatePercentage();
    }

    private float calculatePercentage() {
        int total = present + absent;
        return total > 0 ? (present * 100f) / total : 0f;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
        this.percentage = calculatePercentage();
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
        this.percentage = calculatePercentage();
    }

    public float getPercentage() {
        return percentage;
    }
}
