package com.example.onlymovie.models;

public class Series {
    private String title;
    private String poster_path;
    private Long id;

    public Series(String title, String poster_path, Long id) {
        this.title = title;
        this.poster_path = poster_path;
        this.id = id;
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
}
