package com.example.hp.popmovies;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MovieDetailsFragment.OnFragmentInteractionListener{

    static Context context;

    static LinearLayout mEmptyFavoriteListLayout;
    static LinearLayout mNoInternetLayoutTopRated;
    static LinearLayout mNoInternetLayoutMostPopular;

    static ProgressBar loadingViewTopRated;
    static ProgressBar loadingViewMostPopular;
    static ProgressBar loadingViewFavorites;

    private static boolean mTopRatedLoaded;
    private static boolean mMostPopularLoaded;

    private static boolean mShowCloudErrorTopRated;
    private static boolean mShowCloudErrorMostPopular;

    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 0;

    public static final int SORT_BY_TOP_RATED = 1;
    public static final int SORT_BY_MOST_POPULAR = 2;
    public static final int SORT_TO_FAVOURITES = 3;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    // TheMovieDB api key
    private static String API_KEY;
    public static MovieDB movieDB;

    public static MovieGridAdapter topRatedMovieAdapter;
    public static MovieGridAdapter mostPopularMovieAdapter;
    public static MovieGridCursorAdapter favoriteMovieAdapter;

    ArrayList<String> moviePosterPathListTopRated;
    ArrayList<String> moviePosterPathListMostPopular;

    static SwipeRefreshLayout mMostPopRefreshLayout;
    static SwipeRefreshLayout mTopRatedRefreshLayout;

    static int sortOrder;

    private ViewPager mViewPager;
    static TabLayout tabLayout;

    public static boolean mTwoPane;

    private int[] tabHeaderImageIdsUnselected = {
            R.drawable.tab_header_star_unselected,
            R.drawable.tab_header_like_unselected,
            R.drawable.tab_header_heart_unseleted
    };

    private int[] tabHeaderImageIdsSelected = {
            R.drawable.tab_header_star_selected,
            R.drawable.tab_header_like_selected,
            R.drawable.tab_header_heart_selected
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }else{
            mTwoPane = false;
        }

        getInternetPermission();
        API_KEY = getString(R.string.movies_api_key);

        mEmptyFavoriteListLayout = (LinearLayout) findViewById(R.id.empty_favorites_list_layout);

        movieDB=new MovieDB(API_KEY);

        mTopRatedLoaded = false;
        mMostPopularLoaded = false;
        mShowCloudErrorTopRated = false;
        mShowCloudErrorMostPopular = false;

        moviePosterPathListTopRated=new ArrayList<>();
        moviePosterPathListMostPopular=new ArrayList<>();

        final View layoutView = findViewById(R.id.main_content);

        topRatedMovieAdapter=new MovieGridAdapter(
                this,
                R.layout.movie_grid_item_simple,
                moviePosterPathListTopRated,mTwoPane);

        mostPopularMovieAdapter=new MovieGridAdapter(
                this,
                R.layout.movie_grid_item_simple,
                moviePosterPathListMostPopular,mTwoPane);

        favoriteMovieAdapter = new MovieGridCursorAdapter(this,null,0,mTwoPane);
        movieDB.setOnDataBaseReadyListener(new MovieDB.OnDataRetrievedListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onRetrieved() {
                if(MovieDB.topRatedMovies!=null) {
                    mNoInternetLayoutTopRated.setVisibility(View.GONE);
                    topRatedMovieAdapter.clear();
                    moviePosterPathListTopRated.clear();
                    for (int i = 0; i < MovieDB.topRatedMovies.size(); i++) {
                        moviePosterPathListTopRated.add(MovieDB.topRatedMovies.get(i).mPosterPath);
                    }
                }else{
                    if(mNoInternetLayoutTopRated!=null) {
                        mNoInternetLayoutTopRated.setVisibility(View.VISIBLE);
                        ImageView errorIconIV = (ImageView) mNoInternetLayoutTopRated.findViewById(R.id.error_icon);
                        errorIconIV.setImageResource(R.drawable.cloud_error);
                        errorIconIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                        TextView errorTV = (TextView) mNoInternetLayoutTopRated.findViewById(R.id.error_message);
                        errorTV.setText("Couldn't load data. Please check your internet connection!");
                        errorTV.setTextColor(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                    }else {
                        mShowCloudErrorTopRated = true;
                    }
                }
                if(mTopRatedRefreshLayout!=null) {
                    mTopRatedRefreshLayout.setRefreshing(false);
                }
                mTopRatedLoaded = true;
                if(loadingViewTopRated!=null){
                    loadingViewTopRated.setVisibility(View.GONE);
                }
                if(MovieDB.mostPopularMovies!=null) {
                    mNoInternetLayoutMostPopular.setVisibility(View.GONE);
                    mostPopularMovieAdapter.clear();
                    moviePosterPathListMostPopular.clear();
                    for (int i = 0; i < MovieDB.mostPopularMovies.size(); i++) {
                        moviePosterPathListMostPopular.add(MovieDB.mostPopularMovies.get(i).mPosterPath);
                    }
                }else {
                    if(mNoInternetLayoutMostPopular!=null) {
                        mNoInternetLayoutMostPopular.setVisibility(View.VISIBLE);
                        ImageView errorIconIV = (ImageView) mNoInternetLayoutMostPopular.findViewById(R.id.error_icon);
                        errorIconIV.setImageResource(R.drawable.cloud_error);
                        errorIconIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                        TextView errorTV = (TextView) mNoInternetLayoutMostPopular.findViewById(R.id.error_message);
                        errorTV.setText("Couldn't load data. Please check your internet connection!");
                        errorTV.setTextColor(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                    }else {
                        mShowCloudErrorMostPopular = true;
                    }
                }
                if(mMostPopRefreshLayout!=null) {
                    mMostPopRefreshLayout.setRefreshing(false);
                }
                mMostPopularLoaded = true;
                if(loadingViewMostPopular!=null){
                    loadingViewMostPopular.setVisibility(View.GONE);
                }

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTV = (TextView) findViewById(R.id.app_title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/lemon_milk.otf");
        if (titleTV != null) {
            titleTV.setTypeface(type);
            titleTV.setText(R.string.app_name);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        if (mViewPager != null) {
            mViewPager.setCurrentItem(1);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        if(mTwoPane) {
            tabLayout.getTabAt(0).setIcon(tabHeaderImageIdsUnselected[0]);
            tabLayout.getTabAt(1).setIcon(tabHeaderImageIdsSelected[1]);
            tabLayout.getTabAt(2).setIcon(tabHeaderImageIdsUnselected[2]);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(mTwoPane) {
                    int tabPosition = tab.getPosition();
                    for(int i=0;i<3;i++){
                        if(i==tabPosition){
                            tabLayout.getTabAt(i).setIcon(tabHeaderImageIdsSelected[i]);
                        }else{
                            tabLayout.getTabAt(i).setIcon(tabHeaderImageIdsUnselected[i]);
                        }
                    }
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getSupportLoaderManager().initLoader(MovieDB.FAVS_LOADER_ID,null,this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        GridView mMoviesGrid;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mMoviesGrid = (GridView) rootView.findViewById(R.id.pop_movies_grid_view);
            if(mTwoPane){
                mMoviesGrid.setNumColumns(1);
            }else {
                mMoviesGrid.setNumColumns(2);
            }

            int inflationSortOrder = getArguments().getInt(ARG_SECTION_NUMBER);


            switch (inflationSortOrder){
                case SORT_BY_TOP_RATED:{
                    mMoviesGrid.setAdapter(topRatedMovieAdapter);
                    mNoInternetLayoutTopRated = (LinearLayout) rootView.findViewById(R.id.empty_favorites_list_layout);
                    loadingViewTopRated = (ProgressBar) rootView.findViewById(R.id.loadingAnimation);
                    mTopRatedRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
                    if(mTopRatedLoaded == true){
                        loadingViewTopRated.setVisibility(View.GONE);
                    }
                    if(mShowCloudErrorTopRated == true){
                        mNoInternetLayoutTopRated.setVisibility(View.VISIBLE);
                        ImageView errorIconIV = (ImageView) mNoInternetLayoutTopRated.findViewById(R.id.error_icon);
                        errorIconIV.setImageResource(R.drawable.cloud_error);
                        errorIconIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                        TextView errorTV = (TextView) mNoInternetLayoutTopRated.findViewById(R.id.error_message);
                        errorTV.setText("Couldn't load data. Please check your internet connection!");
                        errorTV.setTextColor(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                    }
                }break;
                case SORT_BY_MOST_POPULAR:{
                    mMoviesGrid.setAdapter(mostPopularMovieAdapter);
                    mNoInternetLayoutMostPopular = (LinearLayout) rootView.findViewById(R.id.empty_favorites_list_layout);
                    loadingViewMostPopular = (ProgressBar) rootView.findViewById(R.id.loadingAnimation);
                    mMostPopRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
                    if(mMostPopularLoaded){
                        loadingViewMostPopular.setVisibility(View.GONE);
                    }
                    if(mShowCloudErrorMostPopular == true){
                        mNoInternetLayoutMostPopular.setVisibility(View.VISIBLE);
                        ImageView errorIconIV = (ImageView) mNoInternetLayoutMostPopular.findViewById(R.id.error_icon);
                        errorIconIV.setImageResource(R.drawable.cloud_error);
                        errorIconIV.setImageTintList(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                        TextView errorTV = (TextView) mNoInternetLayoutMostPopular.findViewById(R.id.error_message);
                        errorTV.setText("Couldn't load data. Please check your internet connection!");
                        errorTV.setTextColor(ColorStateList.valueOf(Color.parseColor("#8e9793")));
                    }
                }break;
                case SORT_TO_FAVOURITES:{
                    mMoviesGrid.setAdapter(favoriteMovieAdapter);
                    ((SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout)).setEnabled(false);
                    mEmptyFavoriteListLayout = (LinearLayout) rootView.findViewById(R.id.empty_favorites_list_layout);
                    loadingViewFavorites = (ProgressBar) rootView.findViewById(R.id.loadingAnimation);
                    loadingViewFavorites.setVisibility(View.GONE);
                }break;
                default:break;
            }

            if (inflationSortOrder == SORT_TO_FAVOURITES) {
                if (MovieDB.favMovies != null) {
                    if (MovieDB.favMovies.size() == 0) {
                        if (mEmptyFavoriteListLayout != null) {
                            mEmptyFavoriteListLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }


            mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie clickedMovie;
                    if(tabLayout.getSelectedTabPosition()+1 == SORT_BY_TOP_RATED){
                        clickedMovie = MovieDB.topRatedMovies.get(position);
                    }else if(tabLayout.getSelectedTabPosition()+1 == SORT_BY_MOST_POPULAR){
                        clickedMovie = MovieDB.mostPopularMovies.get(position);
                    }else if(tabLayout.getSelectedTabPosition()+1 == SORT_TO_FAVOURITES){
                        clickedMovie = MovieDB.favMovies.get(position);
                    }else {
                        clickedMovie = null;
                    }
                    if(clickedMovie!=null) {
                        if(mTwoPane){
                            Bundle arguments = new Bundle();
                            arguments.putSerializable("Movie",clickedMovie);
                            arguments.putBoolean("MovieFavorited",MovieDB.isFavorited(clickedMovie.mId));
                            MovieDetailsFragment fragment = new MovieDetailsFragment();
                            fragment.setArguments(arguments);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.movie_detail_container, fragment)
                                    .commit();
                        }else {
                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Movie", clickedMovie);
                            intent.putExtra("MovieFavorited",MovieDB.isFavorited(clickedMovie.mId));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }

                }
            });
            ((SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    movieDB.refresh();
                }
            });
            return rootView;
        }

        public int getPixelsfromDPs(int px){
            Resources r = getResources();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }



        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:{
                    if(mTwoPane){
                        return null;
                    }else {
                        SpannableString ss1=  new SpannableString("TOP RATED");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 9, 0);
                        return ss1;
                    }
                }
                case 1:{

                    if(mTwoPane){
                        return null;
                    }else {
                        SpannableString ss1=  new SpannableString("MOST POPULAR");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 12, 0);
                        return ss1;
                    }
                }
                case 2: {
                    if(mTwoPane){
                        return null;
                    }else {
                        SpannableString ss1=  new SpannableString("FAVORITES");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 9, 0);
                        return ss1;
                    }
                }
            }
            return null;
        }
    }

    void getInternetPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
                // MY_PERMISSIONS_REQUEST_BLUETOOTH is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public static final int INDEX_ID = 0;
    public static final int INDEX_MOVIE_ID = 1;
    public static final int INDEX_ORIGINAL_TITLE = 2;
    public static final int INDEX_DESCRIPTION =3;
    public static final int INDEX_IS_ADULT = 4;
    public static final int INDEX_BACKDROP_PATH = 5;
    public static final int INDEX_GENRE_IDS = 6;
    public static final int INDEX_ORIGINAL_LANGUAGE = 7;
    public static final int INDEX_RELEASE_DATE = 8;
    public static final int INDEX_POSTER_PATH = 9;
    public static final int INDEX_POPULARITY = 10;
    public static final int INDEX_TITLE = 11;
    public static final int INDEX_VOTE_COUNT = 12;
    public static final int INDEX_VOTE_AVERAGE = 13;

    public static final String[] COLUMN_PROJECTION = new String[]{
            FavDatabase.FavColumns._ID,
            FavDatabase.FavColumns.COLUMN_MOVIE_ID,
            FavDatabase.FavColumns.COLUMN_ORIGINAL_TITLE,
            FavDatabase.FavColumns.COLUMN_DESCRIPTION,
            FavDatabase.FavColumns.COLUMN_IS_ADULT,
            FavDatabase.FavColumns.COLUMN_BACKDROP_PATH,
            FavDatabase.FavColumns.COLUMN_GENRE_IDS,
            FavDatabase.FavColumns.COLUMN_ORIGINAL_LANGUAGE,
            FavDatabase.FavColumns.COLUMN_RELEASE_DATE,
            FavDatabase.FavColumns.COLUMN_POSTER_PATH,
            FavDatabase.FavColumns.COLUMN_POPULARITY,
            FavDatabase.FavColumns.COLUMN_TITLE,
            FavDatabase.FavColumns.COLUMN_VOTE_COUNT,
            FavDatabase.FavColumns.COLUMN_VOTE_AVERAGE
    };

    public static final String[] REVIEW_COLUMN_PROJECTION = new String[]{
            FavDatabase.ReviewColumns._ID,
            FavDatabase.ReviewColumns.COLUMN_REVIEW_ID,
            FavDatabase.ReviewColumns.COLUMN_MOVIE_ID,
            FavDatabase.ReviewColumns.COLUMN_REVIEW_AUTHOR,
            FavDatabase.ReviewColumns.COLUMN_REVIEW_CONTENT,
            FavDatabase.ReviewColumns.COLUMN_REVIEW_URL
    };
    public static final int INDEX_REVIEW_TABLE_ID = 0;
    public static final int INDEX_REVIEW_ID = 1;
    public static final int INDEX_REVIEW_MOVIE_ID = 2;
    public static final int INDEX_REVIEW_AUTHOR = 3;
    public static final int INDEX_REVIEW_CONTENT = 4;
    public static final int INDEX_REVIEW_URL = 5;


    public static final String[] TRAILER_COLUMN_PROJECTION = new String[]{
            FavDatabase.TrailerColumns._ID,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_ID,
            FavDatabase.TrailerColumns.COLUMN_MOVIE_ID,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_LANGUAGE,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_KEY,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_NAME,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_SITE,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_SIZE,
            FavDatabase.TrailerColumns.COLUMN_TRAILER_TYPE
    };

    public static final int INDEX_TRAILER_TABLE_ID = 0;
    public static final int INDEX_TRAILER_ID = 1;
    public static final int INDEX_TRAILER_MOVIE_ID = 2;
    public static final int INDEX_TRAILER_LANGUAGE = 3;
    public static final int INDEX_TRAILER_KEY = 4;
    public static final int INDEX_TRAILER_NAME = 5;
    public static final int INDEX_TRAILER_SITE = 6;
    public static final int INDEX_TRAILER_SIZE = 7;
    public static final int INDEX_TRAILER_TYPE = 8;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavProvider.Favs.FAVS,COLUMN_PROJECTION,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        context = getApplicationContext();
        favoriteMovieAdapter.swapCursor(data);
        MovieDB.favMovies = new ArrayList<>();
        if(data!=null) {
            if (data.moveToFirst()) {
                do {
                    Boolean isAdult;
                    int isAdultInt = data.getInt(INDEX_IS_ADULT);
                    isAdult = isAdultInt == 1;

                    String genreIdsString = data.getString(INDEX_GENRE_IDS);
                    String[] genreIdStrings = genreIdsString.split(" ");
                    int[] genreIds = new int[genreIdStrings.length];
                    int length = genreIdStrings.length;
                    for (int i = 0; i < length; i++) {
                        genreIds[i] = Integer.valueOf(genreIdStrings[i]);
                    }

                    Movie movie = new Movie(
                            data.getLong(INDEX_MOVIE_ID),
                            data.getString(INDEX_ORIGINAL_TITLE),
                            data.getString(INDEX_DESCRIPTION),
                            isAdult,
                            data.getString(INDEX_BACKDROP_PATH),
                            genreIds,
                            data.getString(INDEX_ORIGINAL_LANGUAGE),
                            data.getString(INDEX_RELEASE_DATE),
                            data.getString(INDEX_POSTER_PATH),
                            data.getDouble(INDEX_POPULARITY),
                            data.getString(INDEX_TITLE),
                            data.getInt(INDEX_VOTE_COUNT),
                            data.getDouble(INDEX_VOTE_AVERAGE),
                            null,
                            null
                    );
                    MovieDB.favMovies.add(movie);
                } while (data.moveToNext());
            }

            ReviewLoader reviewLoader = new ReviewLoader();
            reviewLoader.execute();
            TrailerLoader trailerLoader = new TrailerLoader();
            trailerLoader.execute();

            if (MovieDB.favMovies != null) {
                if(mEmptyFavoriteListLayout!=null) {
                    if (MovieDB.favMovies.size() == 0) {
                        mEmptyFavoriteListLayout.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyFavoriteListLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private static class ReviewLoader extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            for(Movie movie:MovieDB.favMovies) {
                Cursor cursor = context.getContentResolver().query(FavProvider.Reviews.reviewFromMovieId(movie.mId), REVIEW_COLUMN_PROJECTION, null, null, null);
                List<MovieReview> reviews = new ArrayList<>();
                if(cursor!=null){
                    if(cursor.moveToFirst()){
                        do{
                            reviews.add(new MovieReview(
                                    cursor.getString(INDEX_REVIEW_ID),
                                    cursor.getString(INDEX_REVIEW_AUTHOR),
                                    cursor.getString(INDEX_REVIEW_CONTENT),
                                    cursor.getString(INDEX_REVIEW_URL)
                            ));
                        }while (cursor.moveToNext());
                    }
                }
                movie.reviews = reviews;
                if(cursor!=null) {
                    cursor.close();
                }
            }return null;
        }
    }

    private static class TrailerLoader extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            for(Movie movie:MovieDB.favMovies) {
                Cursor cursor = context.getContentResolver().query(FavProvider.Trailers.trailerFromMovieId(movie.mId), TRAILER_COLUMN_PROJECTION, null, null, null);
                List<MovieTrailer> trailers = new ArrayList<>();
                if(cursor!=null){
                    if(cursor.moveToFirst()){
                        do{
                            trailers.add(new MovieTrailer(
                                    cursor.getString(INDEX_TRAILER_ID),
                                    cursor.getString(INDEX_TRAILER_LANGUAGE),
                                    cursor.getString(INDEX_TRAILER_KEY),
                                    cursor.getString(INDEX_TRAILER_NAME),
                                    cursor.getString(INDEX_TRAILER_SITE),
                                    cursor.getInt(INDEX_TRAILER_SIZE),
                                    cursor.getString(INDEX_TRAILER_TYPE)
                            ));
                        }while (cursor.moveToNext());
                    }
                }
                movie.trailers = trailers;
                if(cursor!=null) {
                    cursor.close();
                }
            }return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        MovieDB.favMovies = new ArrayList<>();
        favoriteMovieAdapter.swapCursor(null);
    }

    public int getPixelsfromDPs(int px){
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }
}
