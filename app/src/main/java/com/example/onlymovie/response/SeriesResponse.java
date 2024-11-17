package com.example.onlymovie.response;

import com.example.onlymovie.models.Series;

import java.util.List;

public class SeriesResponse {
    private List<Series> results;
    private int page;
    private int totalResults;
    private int totalPages;

    public List<Series> getResults() {
        return results;
    }

    public void setResults(List<Series> results) {
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
