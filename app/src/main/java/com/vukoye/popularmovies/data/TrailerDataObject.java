package com.vukoye.popularmovies.data;


public class TrailerDataObject {

    public TrailerDataObject(int movieId, String id, String type, String size, String site, String key, String name) {
        this.movieId = movieId;
        this.id = id;
        this.type = type;
        this.size = size;
        this.site = site;
        this.key = key;
        this.name = name;
    }

    private int movieId;

    private String id;

    private String type;

    private String size;

    private String site;

    private String key;

    private String name;

    public int getMovieId() {
        return movieId;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
