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
import com.example.patrick.netnix.models.Episode;
import com.example.patrick.netnix.models.Season;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;

import java.util.ArrayList;


public class ShowDetailFragment extends Fragment implements AdapterListener {

    private String query;
    private TextView mTextMessage;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Season> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_detail,
                container, false);

        query = null;
        Bundle args = getArguments();
        if (args != null) {
            query = args.getString("query");
        }

        mTextMessage = (TextView) view.findViewById(R.id.title);
        mTextMessage.setText("show");

        // Use a linear layout manager
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //Specify an adapter
        data = new ArrayList<Season>();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(Object o) {
        try {
            Episode s = (Episode) o;
            Log.d("EPISODE CLICKED", s.getId());

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
