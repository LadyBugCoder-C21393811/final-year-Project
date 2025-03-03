package com.example.fyp;

public class Review {
    private String placeName;
    private String reviewText;
    private String selectedColor;
    private String userId;
    private long timestamp;

    private double latitude; // Added latitude
    private double longitude; // Added longitude

    // Empty constructor for Firebase
    public Review() {}

    public Review(String placeName, String reviewText, String selectedColor, String userId, long timestamp, double latitude, double longitude) {
        this.placeName = placeName;
        this.reviewText = reviewText;
        this.selectedColor = selectedColor;
        this.userId = userId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() { return latitude; }  // Fixed: Added missing getter
    public double getLongitude() { return longitude; }  // Fixed: Added missing getter
}
