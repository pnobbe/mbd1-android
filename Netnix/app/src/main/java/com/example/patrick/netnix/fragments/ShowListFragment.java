package com.example.patrick.netnix.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.patrick.netnix.Cache;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.adapters.AdapterListener;
import com.example.patrick.netnix.adapters.ShowsAdapter;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ShowListFragment extends Fragment implements AdapterListener, AsyncListener {

    private String mQuery;
    private TextView mTextMessage;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Show> mData;
    private AsyncListener mListener = this;
    private int mUpdateCount;
    private int mCallbackCount;
    private String mUpdateString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_list,
                container, false);

        mUpdateCount = 0;
        mCallbackCount = 0;
        mUpdateString = "";

        mTextMessage = (TextView) view.findViewById(R.id.message);

        // Use a linear layout manager
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.shows_recycler_view);

        // Set grid rows to 1 if landscape, 2 if portrait.
        int orientation = getActivity().getResources().getConfiguration().orientation;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 1 : 2, GridLayoutManager.HORIZONTAL, false));

        //Specify an adapter
        mData = new ArrayList<>();
        mAdapter = new ShowsAdapter(mData, getContext(), this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mQuery = null;
        Bundle args = getArguments();
        if (args != null) {
            mQuery = args.getString("query");
            getShows(mQuery);
        } else {
            getMyShows();
        }

        return view;
    }

    public void getMyShows() {
        mTextMessage.setText(getActivity().getString(R.string.my_shows));
        mData.clear();

        // Add entire cache to the data stream.
        mData.addAll(((Cache) this.getActivity().getApplication()).getMyShows());

        if (mData.isEmpty()) {

            // No data in cache.
            mTextMessage.setText(getActivity().getString(R.string.no_shows_followed));
        } else {

            // Refresh view with cached data
            mAdapter.notifyDataSetChanged();

            // Start background thread for each cached show to see if show needs updating.
            for (Show s : mData) {
                checkForUpdates(s);
            }
        }
    }

    public void getShows(final String q) {

        // Clear screen from previous data/query
        mTextMessage.setText("");
        mData.clear();

        if (q == null) {
            getMyShows();
            return;
        }

        ApiService.getInstance(getContext()).getShows(q, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        mTextMessage.setText(getActivity().getString(R.string.search_results_for, q));
                        for (int i = 0; i < response.length(); i++) {
                            Show s = new Show(response.getJSONObject(i).getJSONObject("show"));
                            mData.add(s);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mTextMessage.setText(getActivity().getString(R.string.no_shows_found, q));
                    }
                } catch (final Exception e) {
                    Util.showError(e, getActivity());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Util.showError(e, getActivity());
            }
        });
    }

    public void checkForUpdates(final Show s) {
        ApiService.getInstance(getContext()).getShow(s.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.length() > 0) {
                        Show updatedShow = new Show(response);
                        if (updatedShow.getUpdated().after(s.getUpdated())) {
                            mUpdateCount++;
                            updatedShow.getSeasonsFromApi(getActivity(), getContext(), mListener);
                        }
                    }
                } catch (final Exception e) {
                    Util.showError(e, getActivity());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Util.showError(e, getActivity());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", mQuery);
    }


    @Override
    public void onItemClick(Object o) {
        try {
            ShowDetailFragment sdf = new ShowDetailFragment();
            Show s = (Show) o;
            Bundle b = new Bundle();
            b.putParcelable("show", s);
            sdf.setArguments(b);
            getFragmentManager().beginTransaction().replace(R.id.content, sdf, "showdetail").addToBackStack(null).commit();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(Object o) {
        if (o != null && o instanceof Show) {
            // Show needs updating...
            Show s = (Show) o;
            mCallbackCount++;
            for (Show show : mData) {
                // Find the show that needs the update...
                if (show.getId().equals(s.getId())) {
                    // Found it, update it.
                    show.update(s);
                    mAdapter.notifyDataSetChanged();
                    mUpdateString += (mCallbackCount == mUpdateCount) ? show.getName() : show.getName() + ", ";
                    break;
                }
            }

            if (mCallbackCount == mUpdateCount) {
                Util.showToast(getActivity().getString(R.string.updated_shows, mUpdateString), getActivity());
            }

        }
    }
}
