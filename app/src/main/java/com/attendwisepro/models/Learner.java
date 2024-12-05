package com.attendwisepro.models;

import com.google.gson.annotations.SerializedName;

public class Learner {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("firstName")
    private String firstName;
    
    @SerializedName("lastName")
    private String lastName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("className")
    private String className;
    
    @SerializedName("parentName")
    private String parentName;
    
    @SerializedName("parentContact")
    private String parentContact;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    public Learner(Integer id, String firstName, String lastName, String email, 
                  String phoneNumber, String className, String parentName, 
                  String parentContact) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.className = className;
        this.parentName = parentName;
        this.parentContact = parentContact;
    }

    // Getters
    public Integer getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getClassName() { return className; }
    public String getParentName() { return parentName; }
    public String getParentContact() { return parentContact; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setClassName(String className) { this.className = className; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public void setParentContact(String parentContact) { this.parentContact = parentContact; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Learner learner = (Learner) o;
        return id.equals(learner.id) &&
               firstName.equals(learner.firstName) &&
               lastName.equals(learner.lastName) &&
               (email == null ? learner.email == null : email.equals(learner.email)) &&
               (phoneNumber == null ? learner.phoneNumber == null : phoneNumber.equals(learner.phoneNumber)) &&
               className.equals(learner.className) &&
               parentName.equals(learner.parentName) &&
               parentContact.equals(learner.parentContact);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + className.hashCode();
        result = 31 * result + parentName.hashCode();
        result = 31 * result + parentContact.hashCode();
        return result;
    }
}
