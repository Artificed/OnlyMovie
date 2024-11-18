package com.example.onlymovie.models;

public class Cast {
    private Long id;
    private String name;
    private String profile_path;

    public Cast(Long id, String name, String profile_path) {
        this.id = id;
        this.name = name;
        this.profile_path = profile_path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }
}