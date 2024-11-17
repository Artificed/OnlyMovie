package com.example.onlymovie.models;

public class Movie {
    private String title;
    private String poster_path;
    private Long id;
    private String overview;
    private Long runtime;
    private Double vote_average;

    public Movie(String title, String poster_path, Long id, String overview, Long runtime, Double vote_average) {
        this.title = title;
        this.poster_path = poster_path;
        this.id = id;
        this.overview = overview;
        this.runtime = runtime;
        this.vote_average = vote_average;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Long getRuntime() {
        return runtime;
    }

    public void setRuntime(Long runtime) {
        this.runtime = runtime;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }
}
