package com.example.onlymovie.response;

import com.example.onlymovie.models.Cast;

import java.util.List;

public class PeopleResponse {
    private List<Cast> results;
    private int page;
    private int totalResults;
    private int totalPages;

    public List<Cast> getResults() {
        return results;
    }

    public void setResults(List<Cast> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
