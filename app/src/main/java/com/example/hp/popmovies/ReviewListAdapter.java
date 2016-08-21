package com.example.hp.popmovies;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hp on 8/15/2016.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    List<MovieReview> movieReviews;
    Context context;

    public ReviewListAdapter(Context context, List<MovieReview> movieReviews){
        this.movieReviews = movieReviews;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_item_layout, parent, false);
        return new ViewHolder(view);
    }

    public static int[] avatarIds = new int[]{
            R.drawable.cat_avatar_red_and_black,
            R.drawable.cat_avatar_green_and_grey,
            R.drawable.cat_avatar_blue_and_black,
            R.drawable.cat_avatar_yellow_and_purple,
            R.drawable.ninja_avatar_blue_and_black,
            R.drawable.ninja_avatar_green_and_blue,
            R.drawable.ninja_avatar_yellow_and_purple,
            R.drawable.ninja_avatar_red_and_black
    };

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = movieReviews.get(position);
        String contentToDisplay = holder.mItem.content;
        if(holder.mItem.content.length()>300){
            contentToDisplay = holder.mItem.content.substring(0,299);
            contentToDisplay = contentToDisplay + "...";
            holder.moreOrLessButton.setVisibility(View.VISIBLE);
        }

        holder.mAvatarIV.setImageResource(avatarIds[(int) (Math.random()*7)]);

        final String finalContentToDisplay = contentToDisplay;
        holder.moreOrLessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.moreOrLessButton.getText().toString().compareToIgnoreCase("Show more")==0){
                    holder.moreOrLessButton.setText("Show less");
                    holder.mReviewContentTV.setText(holder.mItem.content);
                }else if(holder.moreOrLessButton.getText().toString().compareToIgnoreCase("Show less")==0){
                    holder.moreOrLessButton.setText("Show more");
                    holder.mReviewContentTV.setText(finalContentToDisplay);
                }
            }
        });
        holder.mReviewerNameTV.setText(holder.mItem.author);
        holder.mReviewContentTV.setText(contentToDisplay);
        if(position==(movieReviews.size()-1)){
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.mView.getLayoutParams();
            layoutParams.bottomMargin+=getPXFromDP(20);
        }
    }

    @Override
    public int getItemCount() {
        return movieReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mReviewerNameTV;
            public final TextView mReviewContentTV;
            public final ImageView mAvatarIV;
            public final Button moreOrLessButton;
            public MovieReview mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mReviewerNameTV = (TextView) view.findViewById(R.id.reviewer_name_text_view);
                mReviewContentTV = (TextView) view.findViewById(R.id.review_content_text_view);
                moreOrLessButton = (Button) view.findViewById(R.id.show_more_or_less_button);
                mAvatarIV = (ImageView) view.findViewById(R.id.avatar_image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mReviewerNameTV.getText() + "'";
            }
        }

    int getPXFromDP(int dp){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
