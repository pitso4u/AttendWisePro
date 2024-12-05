package com.attendwisepro.models;

import com.google.gson.Gson;

public class QRData {
    private String learnerId;
    private String firstName;
    private String lastName;
    private String className;
    private String studentId;

    public QRData(String learnerId, String firstName, String lastName, String className, String studentId) {
        this.learnerId = learnerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.className = className;
        this.studentId = studentId;
    }

    // Convert QR data to JSON string
    public String toJson() {
        return new Gson().toJson(this);
    }

    // Parse JSON string to QRData object
    public static QRData fromJson(String json) {
        return new Gson().fromJson(json, QRData.class);
    }

    // Getters
    public String getLearnerId() { return learnerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getClassName() { return className; }
    public String getStudentId() { return studentId; }

    // Setters
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setClassName(String className) { this.className = className; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    // Get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
