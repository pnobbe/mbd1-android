package com.example.patrick.netnix;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.patrick.netnix.adapters.AdapterListener;
import com.example.patrick.netnix.adapters.ShowsAdapter;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;

import java.util.ArrayList;


public class ShowListFragment extends Fragment implements AdapterListener {

    private String query;
    private TextView mTextMessage;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Show> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_list,
                container, false);

        query = null;
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString("query");
        }

        mTextMessage = (TextView) view.findViewById(R.id.message);

        // Use a linear layout manager
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //Specify an adapter
        data = new ArrayList<Show>();
        mAdapter = new ShowsAdapter(data, getResources().getConfiguration(), getContext(), this);
        mRecyclerView.setAdapter(mAdapter);


        getShows(query);

        return view;
    }

    public void getMyShows() {
        mTextMessage.setText("My shows");
        data.clear();
    }

    public void getShows(final String q) {
        // Clear screen from previous data/query
        mTextMessage.setText("");
        data.clear();
        query = q;

        if (q == null) {
            getMyShows();
            return;
        }

        ApiService.getInstance(getContext()).getShows(q, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        mTextMessage.setText("Search results for '" + q + "'...");
                        for (int i = 0; i < response.length(); i++) {
                            data.add(new Show(response.getJSONObject(i)));
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mTextMessage.setText("No shows found under '" + q + "'.");
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
        outState.putString("query", query);
    }


    @Override
    public void onItemClick(Object o) {
        try {
            Show s = (Show) o;
            getFragmentManager().beginTransaction().replace(R.id.content, new ShowDetailFragment(), "test").addToBackStack(null).commit();
            Log.d("SHOW CLICKED", s.getId() + " " + getFragmentManager().getBackStackEntryCount());

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

}
