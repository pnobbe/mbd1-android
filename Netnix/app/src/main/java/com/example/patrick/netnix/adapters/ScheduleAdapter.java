package com.example.patrick.netnix.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.patrick.netnix.R;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.models.Episode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * ScheduleAdapter, used to display a list of upcoming episodes.
 */
public class ScheduleAdapter extends ArrayAdapter<Episode> {

    private AdapterListener mListener;
    private Activity mActivity;

    public ScheduleAdapter(Context context, ArrayList<Episode> episodes, Activity activity, AdapterListener listener) {
        super(context, 0, episodes);
        this.mListener = listener;
        this.mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Episode ep = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_item, parent, false);
        }

        // Obtain relevant views
        TextView mDay = (TextView) convertView.findViewById(R.id.day);
        TextView mTime = (TextView) convertView.findViewById(R.id.time);
        TextView mShowTitle = (TextView) convertView.findViewById(R.id.show_title);
        TextView mEpisodeTitle = (TextView) convertView.findViewById(R.id.episode_title);

        // Retrieve whether the date is today, tomorrow or within 7 days. Otherwise show regular date format.
        Calendar today = Calendar.getInstance(), tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.DAY_OF_YEAR, tomorrow.get(Calendar.DAY_OF_YEAR) + 1);
        boolean isToday = Util.isSameDay(ep.getAirDate(), today);
        boolean isTomorrow = Util.isSameDay(ep.getAirDate(), tomorrow);
        boolean isWithinSevenDays = Util.isWithinDaysFuture(ep.getAirDate(), 7);

        // Parse date to a user friendly display format
        if (isToday) {

            // If today, present the date as "Today"
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mDay.setText(mActivity.getString(R.string.today));
            mTime.setText(format.format(ep.getAirDate().getTime()));
        } else if (isTomorrow) {

            // If tomorrow, present the date as "Tomorrow"
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mDay.setText(mActivity.getString(R.string.tomorrow));
            mTime.setText(format.format(ep.getAirDate().getTime()));
        } else if (isWithinSevenDays) {

            // If the date falls within 7 days of the current date, present the date in full day name format.
            SimpleDateFormat format1 = new SimpleDateFormat("EEEE", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mDay.setText(format1.format(ep.getAirDate().getTime()));
            mTime.setText(format2.format(ep.getAirDate().getTime()));
        } else {

            // If none of the above, format the date simply as "<month>, <day>"
            SimpleDateFormat format1 = new SimpleDateFormat("MMMM d", Locale.getDefault());
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mDay.setText(format1.format(ep.getAirDate().getTime()));
            mTime.setText(format2.format(ep.getAirDate().getTime()));
        }

        // Set the show name and episode number.
        mShowTitle.setText(ep.getShowName());
        mEpisodeTitle.setText(mActivity.getString(R.string.season_number, ep.getSeasonNumber()) + " " + (mActivity.getString(R.string.episode_number, ep.getNumber())));

        // If an item is clicked, notify the fragment.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(ep);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }


}