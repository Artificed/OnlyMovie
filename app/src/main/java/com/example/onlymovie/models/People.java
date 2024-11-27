package com.example.onlymovie.models;

public class People {
    private String name;
    private String profile_path;
    private String biography;
    private String birthday;
    private Double popularity;
    private String known_for_department;

    public People(String name, String profile_path, String biography, String birthday, Double popularity, String known_for_department) {
        this.name = name;
        this.profile_path = profile_path;
        this.biography = biography;
        this.birthday = birthday;
        this.popularity = popularity;
        this.known_for_department = known_for_department;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
    }
}
