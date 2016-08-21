package com.example.hp.popmovies;

import java.io.Serializable;

/**
 * Created by hp on 8/12/2016.
 */
public class MovieTrailer implements Serializable{
    public String id;
    public String language;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

    public MovieTrailer(String id, String language, String key, String name, String site, int size, String type) {
        this.id = id;
        this.language = language;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }
}
