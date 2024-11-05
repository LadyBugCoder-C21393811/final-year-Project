package com.example.fyp;

public class UserProfile {
    private String username;
    private String gender;
    private String dob;
    private String placeOfOrigin;
    private String disabilityType;
    private String mobilityAids;

    public UserProfile() {}


    public UserProfile(String username, String gender, String dob, String placeOfOrigin, String disabilityType, String mobilityAids) {
        this.username = username;
        this.gender = gender;
        this.dob = dob;
        this.placeOfOrigin = placeOfOrigin;
        this.disabilityType = disabilityType;
        this.mobilityAids = mobilityAids;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public String getDisabilityType() {
        return disabilityType;
    }

    public void setDisabilityType(String disabilityType) {
        this.disabilityType = disabilityType;
    }

    public String getMobilityAids() {
        return mobilityAids;
    }

    public void setMobilityAids(String mobilityAids) {
        this.mobilityAids = mobilityAids;
    }
}
