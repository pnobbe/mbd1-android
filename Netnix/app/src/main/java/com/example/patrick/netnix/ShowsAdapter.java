package com.example.patrick.netnix;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Patrick on 3/21/2017.
 */

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ViewHolder> {
    private ArrayList<Bitmap> mDataset;
    private Configuration mConf;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCard;
        public ViewHolder(CardView v) {
            super(v);
            mCard = v;
        }
    }

    public ShowsAdapter(ArrayList<Bitmap> myDataset, Configuration conf) {
        mDataset = myDataset;
        mConf = conf;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView mImage = (ImageView) holder.mCard.findViewById(R.id.img);
        Log.d("BINDING", position+"");
        mImage.setImageBitmap(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}