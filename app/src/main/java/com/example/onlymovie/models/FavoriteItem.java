package com.example.onlymovie.models;

public class FavoriteItem {
    private Long id;
    private String mediaType;

    public FavoriteItem(Long id, String mediaType) {
        this.id = id;
        this.mediaType = mediaType;
    }

    public Long getId() {
        return id;
    }

    public String getMediaType() {
        return mediaType;
    }
}
