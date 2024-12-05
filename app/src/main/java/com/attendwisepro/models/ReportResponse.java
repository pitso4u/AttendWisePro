package com.attendwisepro.models;

import java.util.List;

public class ReportResponse {
    private int present;
    private int absent;
    private List<AttendanceDetail> attendanceDetails;

    public ReportResponse(int present, int absent, List<AttendanceDetail> attendanceDetails) {
        this.present = present;
        this.absent = absent;
        this.attendanceDetails = attendanceDetails;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public List<AttendanceDetail> getAttendanceDetails() {
        return attendanceDetails;
    }

    public void setAttendanceDetails(List<AttendanceDetail> attendanceDetails) {
        this.attendanceDetails = attendanceDetails;
    }
}
