package com.example.hp.popmovies;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by hp on 8/10/2016.
 */
@ContentProvider(authority = FavProvider.AUTHORITY, database = FavDatabase.FavouritesDatabase.class)
public final class FavProvider {

    public static final String AUTHORITY = "com.example.hp.popmovies";

    @TableEndpoint(table = FavDatabase.FavouritesDatabase.FAVS) public static class Favs {

        @ContentUri(
                path = "favs",
                type = "vnd.android.cursor.dir/fav",
                defaultSort = FavDatabase.FavColumns._ID + " DESC")
        public static final Uri FAVS = Uri.parse("content://" + AUTHORITY + "/favs");
    }

    @TableEndpoint(table = FavDatabase.FavouritesDatabase.REVIEWS) public static class Reviews{
        @ContentUri(
                path = "reviews",
                type = "vnd.android.cursor.dir/review")
        public static final Uri REVIEWS = Uri.parse("content://"+AUTHORITY+"/reviews");

        @InexactContentUri(
                path = "reviews" + "/#",
                name = "REVIEW_ID",
                type = "vnd.android.cursor.dir/reviews",
                whereColumn = FavDatabase.ReviewColumns.COLUMN_MOVIE_ID,
                pathSegment =  1)
        public static Uri reviewFromMovieId(long id){
            return Uri.parse("content://"+AUTHORITY+"/reviews/"+id);
        }
    }

    @TableEndpoint(table = FavDatabase.FavouritesDatabase.TRAILERS) public static class Trailers{
        @ContentUri(
                path = "trailers",
                type = "vnd.android.cursor.dir/trailer")
        public static final Uri TAILERS = Uri.parse("content://"+AUTHORITY+"/trailers");
        @InexactContentUri(
                path = "trailers" + "/#",
                name = "TRAILER_ID",
                type = "vnd.android.cursor.dir/trailers",
                whereColumn = FavDatabase.TrailerColumns.COLUMN_MOVIE_ID,
                pathSegment =  1)
        public static Uri trailerFromMovieId(long id){
            return Uri.parse("content://"+AUTHORITY+"/trailers/"+id);
        }
    }
}