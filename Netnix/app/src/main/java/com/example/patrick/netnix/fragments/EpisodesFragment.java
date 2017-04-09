package com.example.patrick.netnix.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.adapters.AdapterListener;
import com.example.patrick.netnix.adapters.EpisodeAdapter;
import com.example.patrick.netnix.adapters.SeasonsAdapter;
import com.example.patrick.netnix.models.Episode;
import com.example.patrick.netnix.models.Season;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EpisodesFragment extends Fragment implements AdapterListener {

    private Show mShow;
    private TextView mEpisodesLabel;
    private RecyclerView.Adapter mSeasonAdapter;
    private ListViewCompat mEpisodeList;
    private ArrayList<Season> mSeasonData;
    private EpisodeAdapter mEpisodeAdapter;
    private ArrayList<Episode> mEpisodeData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        RecyclerView mRecyclerView;

        view = inflater.inflate(R.layout.fragment_episodes_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.seasons_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL, false));

        // Check if we are rebuilding the fragment from a previous state.
        Bundle args = getArguments();
        if (args != null) {
            // If so, retrieve the show object.
            mShow = args.getParcelable("show");
        }

        mEpisodesLabel = (TextView) view.findViewById(R.id.episodes_label);

        //Specify an adapter for the season list.
        mSeasonData = mShow.getSeasons();
        mSeasonAdapter = new SeasonsAdapter(mSeasonData, getContext(), this, getActivity());
        mRecyclerView.setAdapter(mSeasonAdapter);

        // Specify an adapter for the episode list.
        mEpisodeData = new ArrayList<>();
        mEpisodeList = (ListViewCompat) view.findViewById(R.id.list);
        mEpisodeAdapter = new EpisodeAdapter(getContext(), mEpisodeData, getActivity());
        mEpisodeList.setAdapter(mEpisodeAdapter);

        // Set onItemClickListener for episodes.
        mEpisodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = mEpisodeData.get(position);
                // Flip the isWatched value.
                e.flipIsWatched();

                // Flipping isWatched affects how both the season and the episode are drawn. Redraw them.
                mEpisodeAdapter.notifyDataSetChanged();
                mSeasonAdapter.notifyDataSetChanged();
            }
        });

        if (!mSeasonData.isEmpty()) {
            setEpisodes(mSeasonData.get(0));
        }
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Preserve the show that we are viewing when the fragment is rebuilt.
        outState.putParcelable("show", mShow);
    }


    @Override
    public void onPause() {
        super.onPause();

        // Save the new state to local storage.
        mShow.saveToSharedPreference(getActivity());
    }

    @Override
    public void onItemClick(Object o) {

        // When the user clicks on a season image, present the new view.
        setEpisodes((Season) o);
    }

    /**
     * (Re)populate the listview content with the given season's episodes.
     *
     * @param s -   Season to extract episodes from
     */
    public void setEpisodes(Season s) {
        mEpisodeData.clear();

        if (s.getEpisodes() != null) {
            mEpisodeAdapter.addAll(s.getEpisodes());
            mEpisodesLabel.setText(getActivity().getString(R.string.season_number, s.getNumber()));
        } else {
            mEpisodesLabel.setText(getActivity().getString(R.string.no_episodes_available));
        }

        mEpisodeAdapter.notifyDataSetChanged();
        mSeasonAdapter.notifyDataSetChanged();
    }

}
