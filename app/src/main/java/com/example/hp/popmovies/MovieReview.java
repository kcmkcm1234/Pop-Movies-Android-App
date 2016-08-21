package com.example.hp.popmovies;

import java.io.Serializable;

/**
 * Created by hp on 8/12/2016.
 */
public class MovieReview implements Serializable {
    public String id;
    public String author;
    public String content;
    public String url;

    public MovieReview(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }
}
