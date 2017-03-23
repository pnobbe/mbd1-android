package com.example.patrick.netnix.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.example.patrick.netnix.ShowListFragment;
import com.example.patrick.netnix.services.ApiService;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.models.Show;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * ShowsAdapter manages the card layout that represents the shows.
 */

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ViewHolder> {
    private ArrayList<Show> mDataset;
    private Configuration mConf;
    private Context mContext;
    private AdapterListener mListener;
    private Typeface typeface;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCard;
        public ViewHolder(CardView v) {
            super(v);
            mCard = v;

        }
    }

    public ShowsAdapter(ArrayList<Show> myDataset, Configuration conf, Context cont, AdapterListener listener) {
        mDataset = myDataset;
        mConf = conf;
        mContext = cont;
        mListener = listener;

        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public ShowsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v;
        if (mConf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.show_horizontal, parent, false);

        } else {
            v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.show, parent, false);

        }
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ImageView mImage = (ImageView) holder.mCard.findViewById(R.id.img);

        // Set title
        TextView mText = (TextView) holder.mCard.findViewById(R.id.name);
        mText.setTypeface(typeface);
        mText.setText(mDataset.get(position).getName());

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mDataset.get(position));
            }
        });

        // If the show has no image, don't attempt to retrieve it with the ImageLoader.
        if (mDataset.get(position).getImageURL() == null) {
            mImage.setImageBitmap(getDefaultImage());
            return;
        }

        // Get the image asynchronously through the ImageLoader.
        ApiService.getInstance(mContext).getImageLoader().get(mDataset.get(position).getImageURL(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    mImage.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mImage.setImageBitmap(getDefaultImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*
    * Get a default image from our asset storage.
    */
    private Bitmap getDefaultImage() {
        try {
            InputStream ims = mContext.getAssets().open("images/undefined.png");
            return BitmapFactory.decodeStream(ims);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}