package com.example.patrick.netnix.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patrick.netnix.Cache;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.adapters.AdapterListener;
import com.example.patrick.netnix.adapters.ScheduleAdapter;
import com.example.patrick.netnix.models.Episode;
import com.example.patrick.netnix.models.Show;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ScheduleFragment extends Fragment implements AsyncListener, AdapterListener {

    private ArrayList<Episode> mScheduleData;
    private ScheduleAdapter mScheduleAdapter;
    private final AsyncListener mSelf = this;

    private static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule,
                container, false);

        // Set up the schedule ListView with adapter.
        ListViewCompat mSchedule = (ListViewCompat) view.findViewById(R.id.schedule);
        mScheduleData = new ArrayList<>();
        mScheduleAdapter = new ScheduleAdapter(getContext(), mScheduleData, getActivity(), this);
        mSchedule.setAdapter(mScheduleAdapter);

        // Get shows from cache and retrieve their latest episodes.
        ArrayList<Show> cache = ((Cache) this.getActivity().getApplication()).getMyShows();
        for (Show s : cache) {
            s.getNextEpisode(getContext(), getActivity(), mSelf);
        }

        // Set message
        TextView mMessage = (TextView) view.findViewById(R.id.message);
        if (cache.isEmpty()) {
            mMessage.setText(getActivity().getString(R.string.no_shows_followed));
        }else {
            mMessage.setText(getActivity().getString(R.string.known_schedule));
        }

        // Show message to clarify agenda functionality
        Util.showToast(getActivity().getString(R.string.agenda_toast), getActivity());
        return view;
    }

    @Override
    public void callback(Object o) {

        // Add the callback object (episode) to the list and sort the list to get chronological order.
        mScheduleData.add((Episode) o);
        Collections.sort(mScheduleData, new Comparator<Episode>() {
            public int compare(Episode e1, Episode e2) {
                if (e1.getAirDate().equals(e2.getAirDate()))
                    return 0;
                return (e1.getAirDate().before(e2.getAirDate()) ? -1 : 1);
            }
        });
        mScheduleAdapter.notifyDataSetChanged();
    }

    /**
     * Calendar event
     */
    // Keep the episode in memory while asking permission.
    private Episode epToAdd = null;

    @Override
    public void onItemClick(Object o) {
        Episode ep = (Episode) o;
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Add episode to memory for later
            epToAdd = ep;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, PERMISSIONS_REQUEST_WRITE_CALENDAR);

        } else {
            // Add episode to calendar
            ep.addToCalendar(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_CALENDAR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (epToAdd != null) {
                        epToAdd.addToCalendar(getActivity());
                        epToAdd = null;
                    }
                }
                else {
                    Util.showToast("Permission denied.", getActivity());
                }
            }
        }
    }
}
