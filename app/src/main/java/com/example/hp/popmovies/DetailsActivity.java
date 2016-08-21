package com.example.hp.popmovies;

import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.pixplicity.multiviewpager.MultiViewPager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.movie_title_text_view) TextView movieTitleTV;
    @BindView(R.id.total_rating_text_view) TextView totalRatingTV;
    @BindView(R.id.rating_text_view) TextView ratingTV;
    @BindView(R.id.genres_text_view) TextView genresTV;
    @BindView(R.id.release_date_text_view) TextView releaseDateTV;
    @BindView(R.id.number_of_ratings_text_view) TextView totalVotesTV;
    @BindView(R.id.details_movie_poster_image_view) ImageView moviePostImageView;
    @BindView(R.id.movie_data_linear_layout) LinearLayout movieDataLinearLayout;
    @BindView(R.id.favortie_movie_image_button) ImageView favMovieIB;
    @BindView(R.id.movie_reviews_list) RecyclerView movieReviewsList;
    @BindView(R.id.overview_label_text_view) TextView overviewLabelTV;
    @BindView(R.id.reviews_label_text_view) TextView reviewsLabelTV;
    @BindView(R.id.movie_name_heading_layout) LinearLayout movieHeaderLayout;
    @BindView(R.id.language_text_view) TextView languageTextView;
    Palette colorPallete;
    String movieName;
    ActionBar actionBar;
    boolean mIsFavorited;

    private MultiViewPager mPager;
    public static PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setStatusBarTranslucent(true);
        ButterKnife.bind(this);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);



        window.setStatusBarColor(Color.parseColor("#00ffffff"));

        Bundle extras = getIntent().getExtras();
        final Movie movie = (Movie) extras.getSerializable("Movie");
        movieName = movie.mOriginalTitle;
        mPager = (MultiViewPager) findViewById(R.id.trailer_video_view_pager);
        mPagerAdapter = new YoutubeTrailerAdapter(getSupportFragmentManager(),movie.trailers,movie.mOriginalTitle);
        mPager.setAdapter(mPagerAdapter);
        String baseURL = "http://image.tmdb.org/t/p/w780";
        String baseURLForAccent = "http://image.tmdb.org/t/p/w92";

        mIsFavorited = getIntent().getBooleanExtra("MovieFavorited",false);
        Log.v("MovieFaved: ",""+mIsFavorited);

        Picasso.with(getApplicationContext()).load(baseURL+movie.mPosterPath).into(moviePostImageView);

        TextView movieOverviewTV = (TextView) findViewById(R.id.movie_overview_text_view);
        movieOverviewTV.setText(movie.mDescription);

        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        ratingTV.setText(""+movie.mAverageRating);
        totalVotesTV.setText(""+movie.mVoteCount);
        movieTitleTV.setText(movieName);

        setToobarHeight();

        languageTextView.setText(movie.mOriginalLanguage.toUpperCase());
        releaseDateTV.setText(Utils.getFormattedDate(movie.mReleaseDate));



        GenreIdToName genreIdToNameConveter = new GenreIdToName(getString(R.string.movies_api_key));
        genreIdToNameConveter.setOnGenresRetrievedListener(new GenreIdToName.OnGenresRetrievedListener() {
            @Override
            public void onRetrieved(HashMap<Integer, String> genreIdtoName) {
                String genresString = "";
                for(int i=0;i<movie.mGenreIds.length;i++){
                    if(i!=0 && i!=movie.mGenreIds.length){
                        genresString = genresString +", ";
                    }
                    genresString = genresString +genreIdtoName.get(movie.mGenreIds[i]);
                }
                genresTV.setText(genresString);
            }
        });

        favMovieIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsFavorited){
                    favMovieIB.setImageResource(R.drawable.heart_big);
                    MovieDeleter movieDeleter = new MovieDeleter();
                    movieDeleter.execute(movie.mId);
                    mIsFavorited = false;
                }else{
                    favMovieIB.setImageResource(R.drawable.heart_filled);
                    MovieInjector movieInjector = new MovieInjector();
                    movieInjector.execute(movie);
                    mIsFavorited = true;
                }
            }
        });
        if(mIsFavorited){
            favMovieIB.setImageResource(R.drawable.heart_filled);
        }

        if(movie.reviews.size() == 0){
            reviewsLabelTV.setText("No Reviews Available");
            RelativeLayout.LayoutParams movieReviewsListParams = (RelativeLayout.LayoutParams) movieReviewsList.getLayoutParams();
            movieReviewsListParams.height = getPXFromDP(200);
        }
        movieReviewsList.setAdapter(new ReviewListAdapter(getApplicationContext(),movie.reviews));
        DownloadImage downloadImage = new DownloadImage();
        downloadImage.execute(baseURLForAccent+movie.mPosterPath);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        View v = findViewById(R.id.details_movie_poster_image_view);
        View v1 = findViewById(R.id.filter_layout);
        if (v != null) {
            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0;
            TypedValue tv = new TypedValue();
            CoordinatorLayout.LayoutParams posterLayoutParams = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
            CoordinatorLayout.LayoutParams posterFilterLayoutParams = (CoordinatorLayout.LayoutParams) v1.getLayoutParams();

            posterLayoutParams.topMargin -= paddingTop;
            posterFilterLayoutParams.topMargin -= paddingTop;
        }

        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            if(input!=null) {
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }else{
                return null;
            }
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    class DownloadImage extends AsyncTask<String,String,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {

            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Palette.Builder colorPalleteBuilder;
            super.onPostExecute(bitmap);
            if(bitmap!=null) {
                colorPalleteBuilder= Palette.from(bitmap);
                colorPallete = colorPalleteBuilder.generate();

                setAccentColors(colorPallete);
            }
        }
    }

    class MovieInjector extends AsyncTask<Movie,Void,Void>{
        @Override
        protected Void doInBackground(Movie... params) {
            String genreIds = "";
            int length = params[0].mGenreIds.length;
            for(int i=0;i<length;i++){
                if(i!=(length-1)) {
                    genreIds = genreIds + String.valueOf(params[0].mGenreIds[i]) + " ";
                }else{
                    genreIds = genreIds + String.valueOf(params[0].mGenreIds[i]);
                }
            }
            ContentValues movieCV = new ContentValues();
            movieCV.put(FavDatabase.FavColumns.COLUMN_MOVIE_ID,params[0].mId);
            movieCV.put(FavDatabase.FavColumns.COLUMN_ORIGINAL_TITLE,movieName);
            movieCV.put(FavDatabase.FavColumns.COLUMN_DESCRIPTION,params[0].mDescription);
            movieCV.put(FavDatabase.FavColumns.COLUMN_IS_ADULT,params[0].mIsAdult);
            movieCV.put(FavDatabase.FavColumns.COLUMN_BACKDROP_PATH,params[0].mBackDropPath);
            movieCV.put(FavDatabase.FavColumns.COLUMN_GENRE_IDS,genreIds);
            movieCV.put(FavDatabase.FavColumns.COLUMN_ORIGINAL_LANGUAGE,params[0].mOriginalLanguage);
            movieCV.put(FavDatabase.FavColumns.COLUMN_RELEASE_DATE,params[0].mReleaseDate);
            movieCV.put(FavDatabase.FavColumns.COLUMN_POSTER_PATH,params[0].mPosterPath);
            movieCV.put(FavDatabase.FavColumns.COLUMN_POPULARITY,params[0].mPopularity);
            movieCV.put(FavDatabase.FavColumns.COLUMN_TITLE,params[0].mTitle);
            movieCV.put(FavDatabase.FavColumns.COLUMN_VOTE_COUNT,params[0].mVoteCount);
            movieCV.put(FavDatabase.FavColumns.COLUMN_VOTE_AVERAGE,params[0].mAverageRating);
            Uri successUri = getContentResolver().insert(FavProvider.Favs.FAVS,movieCV);
            for(MovieReview review:params[0].reviews){
                ContentValues reviewCV = new ContentValues();
                reviewCV.put(FavDatabase.ReviewColumns.COLUMN_MOVIE_ID,params[0].mId);
                reviewCV.put(FavDatabase.ReviewColumns.COLUMN_REVIEW_ID,review.id);
                reviewCV.put(FavDatabase.ReviewColumns.COLUMN_REVIEW_AUTHOR,review.author);
                reviewCV.put(FavDatabase.ReviewColumns.COLUMN_REVIEW_CONTENT,review.content);
                reviewCV.put(FavDatabase.ReviewColumns.COLUMN_REVIEW_URL,review.url);
                Uri successReviewUri = getContentResolver().insert(FavProvider.Reviews.REVIEWS,reviewCV);
                Log.v("ReviewUri"," "+successReviewUri.toString());
            }
            for(MovieTrailer trailer:params[0].trailers){
                ContentValues trailerCV = new ContentValues();
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_MOVIE_ID,params[0].mId);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_ID,trailer.id);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_LANGUAGE,trailer.language);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_KEY,trailer.key);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_NAME,trailer.name);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_SITE,trailer.site);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_SIZE,trailer.size);
                trailerCV.put(FavDatabase.TrailerColumns.COLUMN_TRAILER_TYPE,trailer.type);
                Uri successTrailerUri = getContentResolver().insert(FavProvider.Trailers.TAILERS,trailerCV);
                Log.v("ReviewUri"," "+successTrailerUri.toString());
            }
            Log.v("FavUri", " "+successUri.toString());
            return null;
        }
    }

    class MovieDeleter extends AsyncTask<Long,Void,Void>{
        @Override
        protected Void doInBackground(Long... params) {
            String whereArgument = FavDatabase.FavColumns.COLUMN_MOVIE_ID + "=?";
            int deleted = getContentResolver().delete(FavProvider.Favs.FAVS,whereArgument,new String[]{String.valueOf(params[0])});

            String whereArgumentReviews = FavDatabase.ReviewColumns.COLUMN_MOVIE_ID + "=?";
            String whereArgumentTrailers = FavDatabase.TrailerColumns.COLUMN_MOVIE_ID + "=?";

            int deletedReviews = getContentResolver().delete(
                    FavProvider.Reviews.REVIEWS,
                    whereArgumentReviews,
                    new String[]{String.valueOf(params[0])});

            Log.v("DeletedReviews: "," "+deletedReviews);

            int deletedTrailers = getContentResolver().delete(
                    FavProvider.Trailers.TAILERS,
                    whereArgumentTrailers,
                    new String[]{String.valueOf(params[0])});

            Log.v("DeletedTrailers:"," "+deletedTrailers);

            Log.v("DeletedInt:", " "+deleted);
            return null;
        }
    }

    int brightenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f; // value component
        return Color.HSVToColor(hsv);
    }


    int darkenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    int getInverseRGBColor(int color){
        int oppositeRed = 255 - Color.red(color);
        int oppositeGreen = 255 - Color.green(color);
        int oppositeBlue = 255 - Color.blue(color);
        return Color.rgb(oppositeRed,oppositeGreen,oppositeBlue);
    }

    void setToobarHeight(){
        ViewTreeObserver viewTreeObserver = movieTitleTV.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = movieTitleTV.getHeight();
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.getLayoutParams().height = height;
            }
        });

    }
    void setAccentColors(Palette colorPallete){
        int color = colorPallete.getVibrantColor(Color.parseColor("#1F1F1F"));
        int lightMutedColor = colorPallete.getLightMutedColor(Color.parseColor("#FFFFFF"));
        int darkMutedColor = colorPallete.getDarkMutedColor(Color.parseColor("#1F1F1F"));
        int darkVibrantColor = colorPallete.getDarkVibrantColor(Color.parseColor("#1F1F1F"));
        int lightVibrantColor = colorPallete.getLightVibrantColor(Color.parseColor("#FFFFFF"));
        int invColor = getInverseRGBColor(color);
        totalRatingTV.setTextColor(ColorStateList.valueOf(Color.WHITE));
        totalVotesTV.setTextColor(ColorStateList.valueOf(Color.WHITE));
        genresTV.setTextColor(ColorStateList.valueOf(Color.WHITE));
        releaseDateTV.setTextColor(ColorStateList.valueOf(Color.WHITE));
        movieTitleTV.setTextColor(ColorStateList.valueOf(Color.WHITE));
        languageTextView.setTextColor(ColorStateList.valueOf(Color.WHITE));

        LinearLayout infoLayout= (LinearLayout) findViewById(R.id.movie_information_layout);
        infoLayout.setBackgroundColor(darkVibrantColor);

        movieHeaderLayout.setBackgroundColor(darkenColor(darkVibrantColor));
        LinearLayout filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
        filterLayout.setBackgroundColor(setAplaOfColor(color,70));

        TextView movieDescriptionLayout = (TextView) findViewById(R.id.movie_overview_text_view);
        movieDescriptionLayout.setBackgroundColor(darkVibrantColor);

        movieReviewsList.setBackgroundColor(darkVibrantColor);
        overviewLabelTV.setBackgroundColor(darkVibrantColor);
        reviewsLabelTV.setBackgroundColor(darkVibrantColor);
        overviewLabelTV.setTextColor(ColorStateList.valueOf(brightenColor(lightVibrantColor)));
        reviewsLabelTV.setTextColor(ColorStateList.valueOf(brightenColor(lightVibrantColor)));

        mPager.setBackgroundColor(darkVibrantColor);
    }

    int setAplaOfColor(int color,int alpha){
        return Color.argb(alpha, Color.red(color),Color.green(color),Color.blue(color));
    }

    int getPXFromDP(int dp){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
