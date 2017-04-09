package com.example.patrick.netnix.adapters;

import android.app.Activity;
import android.content.Context;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.services.ApiService;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.models.Show;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * ShowsAdapter manages the card layout that represents the shows.
 */

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ViewHolder> {
    private ArrayList<Show> mDataset;
    private Context mContext;
    private AdapterListener mListener;
    private Activity mActivity;
    private Typeface typeface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCard;

        public ViewHolder(CardView v) {
            super(v);
            mCard = v;

        }
    }

    public ShowsAdapter(ArrayList<Show> myDataset, Context cont, AdapterListener listener, Activity a) {
        mDataset = myDataset;
        mContext = cont;
        mListener = listener;
        mActivity = a;

        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public ShowsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v;
        v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.show_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ImageView mImage = (ImageView) holder.mCard.findViewById(R.id.img);
        final Show s = mDataset.get(position);

        if (s.getImageURL() != null) {
            // Get the image asynchronously through the ImageLoader.
            ApiService.getInstance(mContext).getImageLoader().get(s.getImageURL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        mImage.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mImage.setImageBitmap(Util.getDefaultImage(mContext));
                }
            });
        } else {
            mImage.setImageBitmap(Util.getDefaultImage(mContext));
        }

        // Set title
        TextView mText = (TextView) holder.mCard.findViewById(R.id.name);
        AutofitHelper.create(mText);

        mText.setTypeface(typeface);
        mText.setText(s.getName());

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(s);
            }
        });

        // If the show has no image, don't attempt to retrieve it with the ImageLoader.
        if (mDataset.get(position).getImageURL() == null) {
            mImage.setImageBitmap(Util.getDefaultImage(mContext));
            return;
        }

        TextView mToWatch = (TextView) holder.mCard.findViewById(R.id.toWatch);
        if (s.isFollowed(mActivity)) {
            int watched = s.getWatchedEpisodes();
            int total = s.getTotalEpisodes();

            if (watched != 0 || total != 0) {
                if (watched == total) {
                    mToWatch.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAcceptLight));
                    mToWatch.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                } else {
                    mToWatch.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
                    mToWatch.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_dark_background));
                }

                mToWatch.setText(mActivity.getString(R.string.watched_episodes, watched, total));
            }
        } else {
            mToWatch.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}