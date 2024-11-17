package com.example.onlymovie.models;

public class User {
    private String fullName, email, username;

    public User() {}

    public User(String fullName, String email, String username) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {return username;}
}
