package com.example.onlymovie.models;

public class MovieCredit {
    private String title;
    private String poster_path;
    private Long id;
    private String job;

    public MovieCredit(String title, String poster_path, Long id, String job) {
        this.title = title;
        this.poster_path = poster_path;
        this.id = id;
        this.job = job;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
