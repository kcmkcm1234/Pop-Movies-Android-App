package com.example.hp.popmovies;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by hp on 8/10/2016.
 */
public class MovieGridCursorAdapter extends CursorAdapter {

    boolean twoPane;

    Context context;

    static class RecordHolder {
        ImageView imageItem;

        public RecordHolder(View view){
            imageItem = (ImageView) view.findViewById(R.id.movie_poster_image_view);
        }
    }

    public MovieGridCursorAdapter(Context context, Cursor c, int flags,boolean twoPane) {
        super(context, c, flags);
        this.twoPane = twoPane;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item_simple, parent, false);
        RecordHolder viewHolder = new RecordHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RecordHolder viewHolder = (RecordHolder) view.getTag();

        String baseURL = "http://image.tmdb.org/t/p/w500";


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int imageWidth;
        if(twoPane){
            imageWidth = getPXFromDP(250);
        }else {
            imageWidth = size.x/ 2;
        }
        int imageHeight = (imageWidth*277)/185;

        Picasso.with(context).
                load(baseURL+cursor.getString(MainActivity.INDEX_POSTER_PATH)).
                resize(imageWidth,imageHeight).
                centerCrop().
                into(viewHolder.imageItem);
    }

    int getPXFromDP(int dp){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
