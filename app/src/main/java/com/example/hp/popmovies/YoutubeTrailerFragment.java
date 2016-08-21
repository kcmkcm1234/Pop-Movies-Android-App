package com.example.hp.popmovies;

import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

/**
 * Created by hp on 8/12/2016.
 */
public class YoutubeTrailerFragment extends YoutubeFailureRecoveryFragment implements YouTubePlayer.OnFullscreenListener, YouTubePlayer.OnInitializedListener, YouTubeThumbnailView.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    YouTubePlayer youTubePlayer;


    String videoId;
    int skipToMillis;

    public static YoutubeTrailerFragment newInstance(String videoId, int position) {
        YoutubeTrailerFragment fragment = new YoutubeTrailerFragment();
        Bundle args = new Bundle();
        args.putString("video_id",videoId);
        args.putInt("position",position);
        Log.v("VideoId", videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(
                R.layout.trailer_video_item_layout, container, false);

        ImageButton playButton = (ImageButton) rootView.findViewById(R.id.play_youtube_trailer_button);
        Button shareTailerButton = (Button) rootView.findViewById(R.id.share_trailer_button);
        final YouTubePlayer.OnInitializedListener listener = this;
        final FrameLayout trailerFrame = (FrameLayout) rootView.findViewById(R.id.youtube_trailer_frame_layout);
        skipToMillis = -1;

        videoId = getArguments().getString("video_id");
        if(savedInstanceState!=null){
            if(YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]!=null) {
                skipToMillis = savedInstanceState.getInt("milliSeconds");
                YouTubePlayerSupportFragment trailerFragment = YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")];
                // Add the fragment to the 'fragment_container' FrameLayout
                getChildFragmentManager().beginTransaction()
                        .add(R.id.youtube_trailer_frame_layout, trailerFragment).commit();
                trailerFragment.initialize(getString(R.string.youtube_api_key), listener);
            }
        }else {
            if (YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")] != null) {
                try {
                    YoutubeTrailerAdapter.fragmentManagers[getArguments().getInt("position")].beginTransaction().remove(
                            YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]
                    ).commitAllowingStateLoss();
                    YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")].onDestroy();
                    YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")] = null;
                } catch (IllegalStateException exception) {
                    exception.printStackTrace();
                }
            }
        }

        YoutubeTrailerAdapter.fragmentManagers[getArguments().getInt("position")] = getChildFragmentManager();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trailerFrame != null) {
                    skipToMillis = -1;
                    // However, if we're being restored from a previous state,
                    // then we don't need to do anything and should return or else
                    // we could end up with overlapping fragments.
                    //if(YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]!=)
                    if(YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]!=null) {
                        try {
                            YoutubeTrailerAdapter.fragmentManagers[getArguments().getInt("position")].beginTransaction().remove(
                                    YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]
                            ).commitAllowingStateLoss();
                            YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")].onDestroy();
                            YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")] = null;
                        } catch (IllegalStateException exception) {
                            exception.printStackTrace();
                        }
                    }
                    YouTubePlayerSupportFragment trailerFragment = new YouTubePlayerSupportFragment();
                    // Add the fragment to the 'fragment_container' FrameLayout
                    getChildFragmentManager().beginTransaction()
                                .add(R.id.youtube_trailer_frame_layout, trailerFragment).commit();
                    trailerFragment.initialize(getString(R.string.youtube_api_key), listener);

                    YoutubeTrailerAdapter.fragmentManagers[getArguments().getInt("position")] = getChildFragmentManager();

                    YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]=trailerFragment;

                    if(YoutubeTrailerAdapter.currentlyPlayingTrailer!=-1){
                        try {
                            if(YoutubeTrailerAdapter.trailerPlayerFragments[YoutubeTrailerAdapter.currentlyPlayingTrailer]!=null) {
                                YoutubeTrailerAdapter.fragmentManagers[YoutubeTrailerAdapter.currentlyPlayingTrailer].
                                        beginTransaction().remove(
                                        YoutubeTrailerAdapter.trailerPlayerFragments[YoutubeTrailerAdapter.currentlyPlayingTrailer]
                                ).commitAllowingStateLoss();
                                YoutubeTrailerAdapter.trailerPlayerFragments[YoutubeTrailerAdapter.currentlyPlayingTrailer]=null;
                            }
                        }catch (IllegalStateException exception){
                            exception.printStackTrace();
                        }

                        YoutubeTrailerAdapter.trailerPlayerFragments[YoutubeTrailerAdapter.currentlyPlayingTrailer] = null;
                    }

                    YoutubeTrailerAdapter.currentlyPlayingTrailer = getArguments().getInt("position");
                }
            }
        });

        shareTailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, "Trailer: "+YoutubeTrailerAdapter.movieName);
                share.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+videoId);

                startActivity(Intent.createChooser(share, "Share trailer!"));
            }
        });

        YouTubeThumbnailView thumbnailView = (YouTubeThumbnailView) rootView.findViewById(R.id.trailer_youtube_thumbnail_view);
        thumbnailView.initialize(getString(R.string.youtube_api_key),this);

        return rootView;
    }

    @Override
    public void onFullscreen(boolean b) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //nothing
        try {
            if(YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]!=null) {
                YoutubeTrailerAdapter.fragmentManagers[getArguments().getInt("position")].beginTransaction().remove(
                        YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")]
                ).commitAllowingStateLoss();

                if(youTubePlayer!=null) {
                    outState.putInt("milliSeconds", youTubePlayer.getCurrentTimeMillis());
                }else{
                    outState.putInt("milliSeconds", -1);
                }
                //YoutubeTrailerAdapter.trailerPlayerFragments[getArguments().getInt("position")].onDestroy();
            }
        }catch (IllegalStateException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setOnFullscreenListener(this);
        if (!b) {
            videoId = getArguments().getString("video_id");
            if(skipToMillis==-1) {
                Log.v("LoadingVideo:"," "+videoId);
                youTubePlayer.loadVideo(videoId);
            }else{
                Log.v("CueingVideo:"," "+videoId);
                youTubePlayer.cueVideo(videoId,skipToMillis);
                Log.v("MillisToSeek:", " " + skipToMillis/1000);

            }
            this.youTubePlayer = youTubePlayer;
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return new YouTubePlayerSupportFragment();
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
        youTubeThumbnailLoader.setVideo(getArguments().getString("video_id"));

    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
