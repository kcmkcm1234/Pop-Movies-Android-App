package com.example.hp.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 6/18/2016.
 */
public class MovieDB{
    public static final int FAVS_LOADER_ID = 666;

    ArrayList<OnDataRetrievedListener> listeners;
    private String API_KEY;

    private Movie mCurrMovie;
    private ArrayList<Movie> currMovies;
    public static ArrayList<Movie> favMovies;
    public static ArrayList<Movie> topRatedMovies;
    public static ArrayList<Movie> mostPopularMovies;

    public final int GET_MOST_POPULAR = 0;
    public final int GET_TOP_RATED = 1;
    public final int GET_FAV = 2;

    public int sortBy;

    private int index=0;



    public interface OnDataRetrievedListener {
        void onRetrieved();
    }

    public MovieDB(String key){
        listeners = new ArrayList<>();
        API_KEY=key;
        Background background=new Background();
        background.execute();
    }

    public void refresh(){
        Background background=new Background();
        background.execute();
    }

    private ArrayList<Movie> getMostPopular() {
        String responseString;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("movie");
        builder.appendPath("popular");
        builder.appendQueryParameter("api_key", API_KEY);

        Log.i("RequestUrl: ", builder.build().toString());
        try {
            URL url = new URL(builder.build().toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

            //now we have the string
            responseString = convertStreamToString(in);
            httpURLConnection.disconnect();
            ArrayList<Movie> listOfMovies = new ArrayList<>();
            JSONArray allMoviesJSONArray = new JSONObject(responseString).getJSONArray("results");
            int length = allMoviesJSONArray.length();
            for(int i=0;i<length;i++){
                JSONObject currJSONObj = allMoviesJSONArray.getJSONObject(i);
                long movieId = currJSONObj.getLong("id");
                JSONArray genreIdsJSONArray = currJSONObj.getJSONArray("genre_ids");
                int genresLength = genreIdsJSONArray.length();
                int[] genreIds = new int[genreIdsJSONArray.length()];
                for(int j=0;j<genresLength;j++){
                    genreIds[j] = genreIdsJSONArray.getInt(j);
                }

                Uri.Builder trailersUriBuilder = new Uri.Builder();
                trailersUriBuilder.scheme("https");
                trailersUriBuilder.authority("api.themoviedb.org");
                trailersUriBuilder.appendPath("3");
                trailersUriBuilder.appendPath("movie");
                trailersUriBuilder.appendPath(String.valueOf(movieId));
                trailersUriBuilder.appendPath("videos");
                trailersUriBuilder.appendQueryParameter("api_key", API_KEY);

                URL trailersUrl = new URL(trailersUriBuilder.build().toString());
                HttpURLConnection trailersHttpURLConnection = (HttpURLConnection) trailersUrl.openConnection();
                trailersHttpURLConnection.setRequestMethod("GET");
                trailersHttpURLConnection.connect();
                InputStream tailersIn = new BufferedInputStream(trailersHttpURLConnection.getInputStream());

                //now we have the string
                String trailerResponseString = convertStreamToString(tailersIn);
                trailersHttpURLConnection.disconnect();
                JSONArray allTrialersJSONArray = new JSONObject(trailerResponseString).getJSONArray("results");
                List<MovieTrailer> movieTrailers = new ArrayList<>();

                int numtrailers = allTrialersJSONArray.length();
                for(int j=0;j<numtrailers;j++){
                    JSONObject currTrailerJson = allTrialersJSONArray.getJSONObject(j);
                    movieTrailers.add(new MovieTrailer(
                            currTrailerJson.getString("id"),
                            currTrailerJson.getString("iso_639_1"),
                            currTrailerJson.getString("key"),
                            currTrailerJson.getString("name"),
                            currTrailerJson.getString("site"),
                            currTrailerJson.getInt("size"),
                            currTrailerJson.getString("type")
                    ));
                }
                Uri.Builder reviewsUriBuilder = new Uri.Builder();
                reviewsUriBuilder.scheme("https");
                reviewsUriBuilder.authority("api.themoviedb.org");
                reviewsUriBuilder.appendPath("3");
                reviewsUriBuilder.appendPath("movie");
                reviewsUriBuilder.appendPath(String.valueOf(movieId));
                reviewsUriBuilder.appendPath("reviews");
                reviewsUriBuilder.appendQueryParameter("api_key", API_KEY);

                URL reviewsUrl = new URL(reviewsUriBuilder.build().toString());
                HttpURLConnection reviewsHttpURLConnection = (HttpURLConnection) reviewsUrl.openConnection();
                reviewsHttpURLConnection.setRequestMethod("GET");
                reviewsHttpURLConnection.connect();
                InputStream reviewsIn = new BufferedInputStream(reviewsHttpURLConnection.getInputStream());

                //now we have the string
                String reviewsResponseString = convertStreamToString(reviewsIn);
                reviewsHttpURLConnection.disconnect();
                JSONArray allReviewsJSONArray = new JSONObject(reviewsResponseString).getJSONArray("results");
                List<MovieReview> movieReviews = new ArrayList<>();

                int numreviews = allReviewsJSONArray.length();
                for(int j=0;j<numreviews;j++){
                    JSONObject currReviewJson = allReviewsJSONArray.getJSONObject(j);
                    movieReviews.add(new MovieReview(
                            currReviewJson.getString("id"),
                            currReviewJson.getString("author"),
                            currReviewJson.getString("content"),
                            currReviewJson.getString("url")
                    ));
                }

                listOfMovies.add(new Movie(
                        movieId,
                        currJSONObj.getString("original_title"),
                        currJSONObj.getString("overview"),
                        currJSONObj.getBoolean("adult"),
                        currJSONObj.getString("backdrop_path"),
                        genreIds,
                        currJSONObj.getString("original_language"),
                        currJSONObj.getString("release_date"),
                        currJSONObj.getString("poster_path"),
                        currJSONObj.getDouble("popularity"),
                        currJSONObj.getString("title"),
                        currJSONObj.getInt("vote_count"),
                        currJSONObj.getDouble("vote_average"),
                        movieTrailers,
                        movieReviews
                ));
            }
            return listOfMovies;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Movie> getTopRated() {
        String responseString;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("movie");
        builder.appendPath("top_rated");
        builder.appendQueryParameter("api_key", API_KEY);

        Log.i("RequestUrl: ", builder.build().toString());
        try {
            URL url = new URL(builder.build().toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

            //now we have the string
            responseString = convertStreamToString(in);
            httpURLConnection.disconnect();
            ArrayList<Movie> listOfMovies = new ArrayList<>();
            JSONArray allMoviesJSONArray = new JSONObject(responseString).getJSONArray("results");
            int length = allMoviesJSONArray.length();
            for(int i=0;i<length;i++){
                JSONObject currJSONObj = allMoviesJSONArray.getJSONObject(i);
                long movieId = currJSONObj.getLong("id");
                JSONArray genreIdsJSONArray = currJSONObj.getJSONArray("genre_ids");
                int genresLength = genreIdsJSONArray.length();
                int[] genreIds = new int[genreIdsJSONArray.length()];
                for(int j=0;j<genresLength;j++){
                    genreIds[j] = genreIdsJSONArray.getInt(j);
                }

                Uri.Builder trailersUriBuilder = new Uri.Builder();
                trailersUriBuilder.scheme("https");
                trailersUriBuilder.authority("api.themoviedb.org");
                trailersUriBuilder.appendPath("3");
                trailersUriBuilder.appendPath("movie");
                trailersUriBuilder.appendPath(String.valueOf(movieId));
                trailersUriBuilder.appendPath("videos");
                trailersUriBuilder.appendQueryParameter("api_key", API_KEY);

                URL trailersUrl = new URL(trailersUriBuilder.build().toString());
                HttpURLConnection trailersHttpURLConnection = (HttpURLConnection) trailersUrl.openConnection();
                trailersHttpURLConnection.setRequestMethod("GET");
                trailersHttpURLConnection.connect();
                InputStream tailersIn = new BufferedInputStream(trailersHttpURLConnection.getInputStream());

                //now we have the string
                String trailerResponseString = convertStreamToString(tailersIn);
                httpURLConnection.disconnect();
                JSONArray allTrialersJSONArray = new JSONObject(trailerResponseString).getJSONArray("results");
                List<MovieTrailer> movieTrailers = new ArrayList<>();

                int numtrailers = allTrialersJSONArray.length();
                for(int j=0;j<numtrailers;j++){
                    JSONObject currTrailerJson = allTrialersJSONArray.getJSONObject(j);
                    movieTrailers.add(new MovieTrailer(
                            currTrailerJson.getString("id"),
                            currTrailerJson.getString("iso_639_1"),
                            currTrailerJson.getString("key"),
                            currTrailerJson.getString("name"),
                            currTrailerJson.getString("site"),
                            currTrailerJson.getInt("size"),
                            currTrailerJson.getString("type")
                    ));
                }
                Uri.Builder reviewsUriBuilder = new Uri.Builder();
                reviewsUriBuilder.scheme("https");
                reviewsUriBuilder.authority("api.themoviedb.org");
                reviewsUriBuilder.appendPath("3");
                reviewsUriBuilder.appendPath("movie");
                reviewsUriBuilder.appendPath(String.valueOf(movieId));
                reviewsUriBuilder.appendPath("reviews");
                reviewsUriBuilder.appendQueryParameter("api_key", API_KEY);

                URL reviewsUrl = new URL(reviewsUriBuilder.build().toString());
                HttpURLConnection reviewsHttpURLConnection = (HttpURLConnection) reviewsUrl.openConnection();
                reviewsHttpURLConnection.setRequestMethod("GET");
                reviewsHttpURLConnection.connect();
                InputStream reviewsIn = new BufferedInputStream(reviewsHttpURLConnection.getInputStream());

                //now we have the string
                String reviewsResponseString = convertStreamToString(reviewsIn);
                httpURLConnection.disconnect();
                JSONArray allReviewsJSONArray = new JSONObject(reviewsResponseString).getJSONArray("results");
                List<MovieReview> movieReviews = new ArrayList<>();

                int numreviews = allReviewsJSONArray.length();
                for(int j=0;j<numreviews;j++){
                    JSONObject currReviewJson = allReviewsJSONArray.getJSONObject(j);
                    movieReviews.add(new MovieReview(
                            currReviewJson.getString("id"),
                            currReviewJson.getString("author"),
                            currReviewJson.getString("content"),
                            currReviewJson.getString("url")
                    ));
                }

                listOfMovies.add(new Movie(
                        movieId,
                        currJSONObj.getString("original_title"),
                        currJSONObj.getString("overview"),
                        currJSONObj.getBoolean("adult"),
                        currJSONObj.getString("backdrop_path"),
                        genreIds,
                        currJSONObj.getString("original_language"),
                        currJSONObj.getString("release_date"),
                        currJSONObj.getString("poster_path"),
                        currJSONObj.getDouble("popularity"),
                        currJSONObj.getString("title"),
                        currJSONObj.getInt("vote_count"),
                        currJSONObj.getDouble("vote_average"),
                        movieTrailers,
                        movieReviews
                ));
            }
            return listOfMovies;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    void moveTo(int index){
        if(currMovies !=null) {
            try {
                mCurrMovie = currMovies.get(index);
                this.index = index;
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

    }

    void moveToStart(){
        if(currMovies != null) {
            try {
                mCurrMovie = currMovies.get(0);
                index = 0;
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    }

    void nextMovie(){
        try {
            mCurrMovie = currMovies.get(index+1);
            index++;
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public String getMovieOriginalTitle(){
        return mCurrMovie.mOriginalTitle;
    }

    public String getMovieDescription(){
        return mCurrMovie.mDescription;
    }

    public boolean isAdult(){
        return mCurrMovie.mIsAdult;
    }

    public String getBackDropPath(){
        return mCurrMovie.mBackDropPath;
    }

    public Long getId(){
        return mCurrMovie.mId;
    }

    public int[] getGenreIds(){
        return mCurrMovie.mGenreIds;
    }

    public String getOriginalLanguage(){
        return mCurrMovie.mOriginalLanguage;
    }

    public String getMovieReleaseDate(){
        return mCurrMovie.mReleaseDate;
    }

    public String getPosterPath(){
        return mCurrMovie.mPosterPath;
    }

    public Double getMoviePopularity(){
        return mCurrMovie.mPopularity;
    }

    public String getMovieTitle(){
        return mCurrMovie.mTitle;
    }

    public Integer getVoteCount(){
        return mCurrMovie.mVoteCount;
    }

    public Double getAverageRating(){
        return mCurrMovie.mAverageRating;
    }

    public void sortByMostPopular(){
        currMovies = mostPopularMovies;
    }

    public void sortByTopRated(){
        currMovies = topRatedMovies;
    }

    public void sortToFavourites(){
        currMovies = favMovies;
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private class Background extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mostPopularMovies = getMostPopular();
            topRatedMovies = getTopRated();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.v("MovieDB"," Data Retrieved");
            moveToStart();
            for (OnDataRetrievedListener listener: listeners){
                listener.onRetrieved();
            }
        }
    }

    int length(){
        if(currMovies != null){
            return currMovies.size();
        }else {
            return 0;
        }
    }

    void setOnDataBaseReadyListener(OnDataRetrievedListener dataBaseReadyListener){
        this.listeners.add(dataBaseReadyListener);
    }

    static boolean isFavorited(Long mId){
        if(favMovies!=null) {
            for (Movie movie : favMovies) {
                if (movie.mId.equals(mId)) {
                    return true;
                }
            }
            return false;
        }else {
            return false;
        }
    }

}
