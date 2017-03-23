package com.example.patrick.netnix;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.patrick.netnix.adapters.ShowsAdapter;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;

import java.util.ArrayList;


public class ScheduleFragment extends Fragment {

    private TextView mTextMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule,
                container, false);

        mTextMessage = (TextView) view.findViewById(R.id.message);
        mTextMessage.setText("Schedule");

        return view;
    }



}
