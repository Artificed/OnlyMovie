package com.example.onlymovie.utils;

public class Utils {
    public static String getYear(String releaseDate) {
        if (releaseDate != null && releaseDate.length() >= 4) {
            return releaseDate.substring(0, 4);
        }
        return "";
    }
}
