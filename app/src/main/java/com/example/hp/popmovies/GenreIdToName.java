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
import java.util.HashMap;

/**
 * Created by hp on 7/19/2016.
 */
public class GenreIdToName {
    ArrayList<OnGenresRetrievedListener> onGenresRetrievedListeners;
    String API_KEY;
    HashMap<Integer,String> genreIdToName;

    public GenreIdToName(String apiKey){
        API_KEY = apiKey;
        genreIdToName = new HashMap<>();
        onGenresRetrievedListeners = new ArrayList<>();
        Background background = new Background();
        background.execute();

    }

    public interface OnGenresRetrievedListener{
        void onRetrieved(HashMap<Integer,String> genreIdtoName);
    }

    public void setOnGenresRetrievedListener(OnGenresRetrievedListener genresRetrievedListener){
        onGenresRetrievedListeners.add(genresRetrievedListener);
    }

    JSONObject getGenreListObj(){
        String responseString;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("api.themoviedb.org");
        builder.appendPath("3");
        builder.appendPath("genre");
        builder.appendPath("list");
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
            Log.v("Response: ", responseString);
            httpURLConnection.disconnect();
            return new JSONObject(responseString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class Background extends AsyncTask<Void, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... task) {
            return getGenreListObj();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            Log.v("GenreIdToName"," Data Retrieved");
            if(result!=null) {
                if (result.has("genres")) {
                    try {
                        JSONArray genresArray = result.getJSONArray("genres");
                        for (int i = 0; i < genresArray.length(); i++) {
                            JSONObject genreObj = genresArray.getJSONObject(i);
                            genreIdToName.put(genreObj.getInt("id"), genreObj.getString("name"));
                        }
                        if (!genreIdToName.isEmpty()) {
                            for (OnGenresRetrievedListener listener : onGenresRetrievedListeners) {
                                listener.onRetrieved(genreIdToName);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
