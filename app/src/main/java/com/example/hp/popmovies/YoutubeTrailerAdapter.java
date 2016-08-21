package com.example.hp.popmovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 8/12/2016.
 */
public class YoutubeTrailerAdapter extends FragmentStatePagerAdapter {

    List<MovieTrailer> movieTrailers;
    public static int numOfTrailers;
    public static int currentlyPlayingTrailer;
    public static YouTubePlayerSupportFragment[] trailerPlayerFragments;
    public static FragmentManager[] fragmentManagers;
    public static FragmentManager childFragmentManager;
    public static String movieName;

    public YoutubeTrailerAdapter(FragmentManager fm, List<MovieTrailer> movieTrailers1, String movieName) {
        super(fm);

        movieTrailers = new ArrayList<>();
        for(MovieTrailer trailer:movieTrailers1){
            if(trailer.site.equals("YouTube")){
                movieTrailers.add(trailer);
            }else {
                Log.v("NonYoutubeTrailer", trailer.site+trailer.key);
            }
        }
        trailerPlayerFragments = new YouTubePlayerSupportFragment[movieTrailers.size()];
        fragmentManagers = new FragmentManager[movieTrailers.size()];
        currentlyPlayingTrailer = -1;
        numOfTrailers = movieTrailers.size();
        this.movieName = movieName;
        this.childFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position){
        Log.v("YouTraAda:getItem:"," "+position);
        return YoutubeTrailerFragment.newInstance(movieTrailers.get(position).key,position);
    }

    @Override
    public int getCount() {
        return numOfTrailers;
    }
}
