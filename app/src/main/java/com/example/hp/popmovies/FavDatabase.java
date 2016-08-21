package com.example.hp.popmovies;

import android.net.Uri;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Table;
import net.simonvt.schematic.annotation.TableEndpoint;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by hp on 8/10/2016.
 */
public class FavDatabase {

    public interface FavColumns{
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
        @DataType(DataType.Type.INTEGER) @Unique @NotNull String COLUMN_MOVIE_ID = "movie_id";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_ORIGINAL_TITLE = "original_title";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_DESCRIPTION = "overview";
        @DataType(DataType.Type.INTEGER) @NotNull String COLUMN_IS_ADULT = "adult";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_BACKDROP_PATH = "backdrop_path";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_GENRE_IDS = "genre_ids";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_RELEASE_DATE = "release_date";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_POSTER_PATH = "poster_path";
        @DataType(DataType.Type.REAL) @NotNull String COLUMN_POPULARITY = "popularity";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TITLE = "title";
        @DataType(DataType.Type.INTEGER) @NotNull String COLUMN_VOTE_COUNT = "vote_count";
        @DataType(DataType.Type.REAL) @NotNull String COLUMN_VOTE_AVERAGE  = "vote_average";
    }

    public interface ReviewColumns{
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
        @DataType(DataType.Type.TEXT) @NotNull @Unique String COLUMN_REVIEW_ID = "review_id";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_REVIEW_AUTHOR = "review_author";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_REVIEW_CONTENT = "review_content";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_REVIEW_URL = "review_url";
        @DataType(DataType.Type.INTEGER) @NotNull @References(table = FavouritesDatabase.FAVS, column = FavColumns.COLUMN_MOVIE_ID) String COLUMN_MOVIE_ID = "movie_id";
    }

    public interface TrailerColumns{
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
        @DataType(DataType.Type.TEXT) @Unique @NotNull String COLUMN_TRAILER_ID = "trailer_id";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TRAILER_LANGUAGE = "trailer_language";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TRAILER_KEY = "trailer_key";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TRAILER_NAME = "trailer_name";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TRAILER_SITE = "trailer_site";
        @DataType(DataType.Type.INTEGER) @NotNull String COLUMN_TRAILER_SIZE = "trailer_size";
        @DataType(DataType.Type.TEXT) @NotNull String COLUMN_TRAILER_TYPE = "trailer_type";
        @DataType(DataType.Type.INTEGER) @NotNull @References(table = FavouritesDatabase.FAVS, column = FavColumns.COLUMN_MOVIE_ID) String COLUMN_MOVIE_ID = "movie_id";
    }

    @Database(version = FavouritesDatabase.DATABASE_VERSION)
    public final class FavouritesDatabase{
        public static final int DATABASE_VERSION = 2;

        @Table(FavColumns.class) public static final String FAVS = "favourites";
        @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
        @Table(TrailerColumns.class) public static final String TRAILERS = "trailers";
    }

}
