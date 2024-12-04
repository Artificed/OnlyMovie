package com.example.onlymovie.utils;

public class Enum {

    public static enum MEDIATYPE {
        Movie,
        Tv,
        Person
    }

    public static enum KNOWN_FOR_DEPARTMENT {
        Directing,
        Acting
    }

    public static enum JOB {
        Director,
        Actor
    }

    public static enum SearchResultType {
        movie,
        tv,
        person
    }

    public static enum FirebaseCollection {
        users,
        favorites,
    }

    public static enum IntentValue {
        movieId,
        personId,
        seriesId
    }
}
