package com.example.onlymovie.models;

public class Series {
    private String name;
    private String poster_path;
    private Long id;
    private String overview;
    private Double vote_average;

    public Series(String name, String poster_path, Long id, String overview, Double vote_average) {
        this.name = name;
        this.poster_path = poster_path;
        this.id = id;
        this.overview = overview;
        this.vote_average = vote_average;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }
}
