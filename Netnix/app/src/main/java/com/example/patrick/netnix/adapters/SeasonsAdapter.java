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
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.models.Season;
import com.example.patrick.netnix.services.ApiService;

import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * SeasonsAdapter, used to display a list of seasons in Card format.
 */

public class SeasonsAdapter extends RecyclerView.Adapter<SeasonsAdapter.ViewHolder> {

    private ArrayList<Season> mDataset;
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

    public SeasonsAdapter(ArrayList<Season> myDataset, Context cont, AdapterListener listener, Activity a) {
        mDataset = myDataset;
        mContext = cont;
        mListener = listener;
        mActivity = a;
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public SeasonsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.season_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageView mImage = (ImageView) holder.mCard.findViewById(R.id.img);
        final Season s = mDataset.get(position);

        // Get the image asynchronously through the ImageLoader.
        if (mDataset.get(position).getImageURL() != null) {
            ApiService.getInstance(mContext).getImageLoader().get(mDataset.get(position).getImageURL(), new ImageLoader.ImageListener() {
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
        mText.setText(mActivity.getString(R.string.season_number, s.getNumber()));

        TextView mToWatch = (TextView) holder.mCard.findViewById(R.id.toWatch);

        int watched = s.getWatchedEpisodes();
        int total = s.getTotalEpisodes();
        if (watched == total) {
            mToWatch.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAcceptLight));
            mToWatch.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));

        }else {
            mToWatch.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));
            mToWatch.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_dark_background));
        }
        mToWatch.setText(mActivity.getString(R.string.watched_episodes, watched, total));

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(s);
            }
        });
        holder.mCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                s.flipAllWatched();
                mListener.onItemClick(s);
                return true;
            }
        });

        // If the show has no image, don't attempt to retrieve it with the ImageLoader.
        if (mDataset.get(position).getImageURL() == null) {
            mImage.setImageBitmap(Util.getDefaultImage(mContext));
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}