package com.example.hp.popmovies;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hp on 8/9/2016.
 */
public class Movie implements Serializable {
    public Long mId;
    public String mOriginalTitle;
    public String mDescription;
    public Boolean mIsAdult;
    public String mBackDropPath;
    public int[] mGenreIds;
    public String mOriginalLanguage;
    public String mReleaseDate;
    public String mPosterPath;
    public Double mPopularity;
    public String mTitle;
    public Integer mVoteCount;
    public Double mAverageRating;
    public List<MovieTrailer> trailers;
    public List<MovieReview> reviews;

    public Movie(Long mId, String mOriginalTitle, String mDescription, Boolean mIsAdult, String mBackDropPath, int[] mGenreIds, String mOriginalLanguage, String mReleaseDate, String mPosterPath, Double mPopularity, String mTitle, Integer mVoteCount, Double mAverageRating, List<MovieTrailer> trailers, List<MovieReview> reviews) {
        this.mId = mId;
        this.mOriginalTitle = mOriginalTitle;
        this.mDescription = mDescription;
        this.mIsAdult = mIsAdult;
        this.mBackDropPath = mBackDropPath;
        this.mGenreIds = mGenreIds;
        this.mOriginalLanguage = mOriginalLanguage;
        this.mReleaseDate = mReleaseDate;
        this.mPosterPath = mPosterPath;
        this.mPopularity = mPopularity;
        this.mTitle = mTitle;
        this.mVoteCount = mVoteCount;
        this.mAverageRating = mAverageRating;
        this.trailers = trailers;
        this.reviews = reviews;
    }
}
