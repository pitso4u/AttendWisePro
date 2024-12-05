package com.attendwisepro.models;

import java.util.Date;

public class Visitor {
    private String name;
    private String visitorId;
    private String purpose;
    private String hostName;
    private Date checkInTime;
    private Date checkOutTime;
    private String status;

    public Visitor(String name, String visitorId, String purpose, String hostName, 
                  Date checkInTime, Date checkOutTime) {
        this.name = name;
        this.visitorId = visitorId;
        this.purpose = purpose;
        this.hostName = hostName;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = checkOutTime == null ? "CHECKED_IN" : "CHECKED_OUT";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
        this.status = checkOutTime == null ? "CHECKED_IN" : "CHECKED_OUT";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
