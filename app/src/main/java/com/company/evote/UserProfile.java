package com.company.evote;

public class UserProfile {
    private String email;
    private String username;

    // Default constructor required for Firebase
    public UserProfile() {
    }

    public UserProfile(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}

