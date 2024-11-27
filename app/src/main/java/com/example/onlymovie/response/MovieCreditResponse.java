package com.example.onlymovie.response;

import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.MovieCredit;

import java.util.List;

public class MovieCreditResponse {
    private int id;
    private List<MovieCredit> cast;
    private List<MovieCredit> crew;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieCredit> getCast() {
        return cast;
    }

    public void setCast(List<MovieCredit> cast) {
        this.cast = cast;
    }

    public List<MovieCredit> getCrew() {
        return crew;
    }

    public void setCrew(List<MovieCredit> crew) {
        this.crew = crew;
    }
}
