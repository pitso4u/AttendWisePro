package com.attendwisepro.models;

import java.util.Calendar;
import java.util.Date;

public class AttendanceRecord {
    private String id;
    private String learnerId;
    private Date timestamp;
    private AttendanceStatus status;
    private String className;
    private String notes;

    public enum AttendanceStatus {
        PRESENT,    // Before 8:00 AM
        LATE,       // Between 8:00 AM and 10:00 AM
        ABSENT      // After 10:00 AM
    }

    // Constructor
    public AttendanceRecord(String learnerId, Date timestamp) {
        this.learnerId = learnerId;
        this.timestamp = timestamp;
        this.status = calculateStatus(timestamp);
    }

    // Calculate attendance status based on time
    private AttendanceStatus calculateStatus(Date timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour < 8) {
            return AttendanceStatus.PRESENT;
        } else if (hour < 10 || (hour == 10 && minute == 0)) {
            return AttendanceStatus.LATE;
        } else {
            return AttendanceStatus.ABSENT;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLearnerId() { return learnerId; }
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { 
        this.timestamp = timestamp;
        this.status = calculateStatus(timestamp);
    }

    public AttendanceStatus getStatus() { return status; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
