package com.example.patrick.netnix.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.patrick.netnix.R;
import com.example.patrick.netnix.models.Episode;

import java.util.ArrayList;

/**
 * EpisodeAdapter, used to display a list of episodes.
 */

public class EpisodeAdapter extends ArrayAdapter<Episode> {

    private Activity mActivity;

    public EpisodeAdapter(Context context, ArrayList<Episode> episodes, Activity a) {
        super(context, 0, episodes);
        this.mActivity = a;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Episode ep = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.episode, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView watched = (TextView) convertView.findViewById(R.id.watched_label);
        RelativeLayout bg = (RelativeLayout) convertView.findViewById(R.id.ep_bg);

        // Populate the data into the template view using the data object
        name.setText(mActivity.getString(R.string.episode_number, ep.getNumber()));
        if (ep.getName() != null) {
            name.append(" " + ep.getName());
        }


        if (ep.isWatched()) {
            watched.setText(mActivity.getString(R.string.watched));
            watched.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            name.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            bg.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAcceptLight));
        } else {
            watched.setText(mActivity.getString(R.string.not_watched));
            watched.setTextColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
            name.setTextColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
            bg.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}