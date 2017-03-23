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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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
    private TextView mStatus;
    private RatingBar mRating;
    private ImageView mImage;
    private WebView mSummary;

    private RecyclerView.Adapter mAdapter;
    private ArrayList<Season> data;
    private Show show;

    public ShowDetailFragment(Show s) {
        this.show = s;
    }

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
        mStatus = (TextView) view.findViewById(R.id.status);
        mRating = (RatingBar) view.findViewById(R.id.rating);
        mImage = (ImageView) view.findViewById(R.id.image);
        mSummary = (WebView) view.findViewById(R.id.summary);

        mTextMessage.setText(show.getName());
        mStatus.setText(show.getStatus());
        mRating.setRating(show.getRating());
        mSummary.loadData(show.getSummary(), "text/html; charset=utf-8", "utf-8");
        Log.d("Rating", show.getRating()+"");

        // Get the image asynchronously through the ImageLoader.
        ApiService.getInstance(getContext()).getImageLoader().get(show.getImageURL(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    mImage.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                mImage.setImageBitmap(Util.getDefaultImage(getContext()));
            }
        });

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
