package com.attendwisepro.models;

import java.util.Date;

public class Incident {
    private String id;
    private String title;
    private String description;
    private Date date;
    private String location;
    private String reportedBy;
    private String status;
    private String severity;
    private String resolution;

    public Incident(String title, String description, Date date, String location, 
                   String reportedBy, String severity) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.reportedBy = reportedBy;
        this.severity = severity;
        this.status = "OPEN"; // Default status
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
