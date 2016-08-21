package com.example.hp.popmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hp on 6/18/2016.
 */
public class MovieGridAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> posterPaths = new ArrayList<String>();
    int layoutResourceId;
    boolean twoPane;
    public MovieGridAdapter(Context context, int layoutResourceId, ArrayList<String> posterPaths, boolean twoPane)
    {
        super(context, layoutResourceId, posterPaths);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.posterPaths = posterPaths;
        this.twoPane = twoPane;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;
        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.movie_poster_image_view);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

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
                load(baseURL+posterPaths.get(position)).
                resize(imageWidth,imageHeight).
                centerCrop().
                into(holder.imageItem);

        return row;
    }

    static class RecordHolder {
        ImageView imageItem;
    }

    int getPXFromDP(int dp){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
