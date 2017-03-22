package com.example.patrick.netnix;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageView mImage;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout mContent;

    private ArrayList<Bitmap> data;


    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shows:
                    makeRequest("Gotham");
                    return true;
                case R.id.navigation_episodes:
                    makeRequest("Suits");
                    return true;
                case R.id.navigation_schedule:
                    makeRequest("Game of Thrones");
                    return true;
            }
            return false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleIntent(getIntent());


        mTextMessage = (TextView) findViewById(R.id.message);
        mContent = (LinearLayout) findViewById(R.id.content);

        // use a linear layout manager
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // specify an adapter
        data = new ArrayList<Bitmap>();
        mAdapter = new ShowsAdapter(data, getResources().getConfiguration());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("Change detected", mAdapter.getItemCount() + "");
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            makeRequest(query);
        }
    }

    private void makeRequest(final String show) {
        mTextMessage.setText("");

        String url = "http://api.tvmaze.com/search/shows?q=" + Html.escapeHtml(show);
        ApiService.getInstance(this).getShows(show, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        mTextMessage.setText("Search results for '" + show + "'...");

                        data.clear();
                        Log.d("SIZE", response.length() +"");
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject result = response.getJSONObject(i);
                            JSONObject show = result.getJSONObject("show");

                            String imgUrl = null;
                            try {
                                Log.d("Name", show.getString("name"));
                                Log.d("Object", result+"");
                                if (!show.isNull("image")) {
                                    imgUrl = show.getJSONObject("image").getString("original");
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            } finally {
                                    addImage(imgUrl);
                            }
                        }
                    } else {
                        mTextMessage.setText("No shows found under '" + show + "'.");
                    }
                } catch (final Exception e) {
                    showError(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                showError(e);
            }
        });
    }

    private Bitmap getDefaultImage() {
        Bitmap d = null;
        try {
            // get input stream
            InputStream ims = getAssets().open("undefined.png");

            // load image as Drawable
            d = BitmapFactory.decodeStream(ims);

        } catch(IOException e) {
            showError(e);
        }
        return d;
    }

    private void addDefaultImage() {
        data.add(getDefaultImage());
        mAdapter.notifyDataSetChanged();
    }

    private void addImage(String url) {
        if (url == null) {
            addDefaultImage();
            return;
        }

        ApiService.getInstance(this).getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() == null) {
                    return;
                }
                data.add(response.getBitmap());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                addDefaultImage();
            }
        });
    }



    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tools, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public void showError(final Exception message) {
        Log.e("Volley Error", message.getMessage());
        message.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        message.getMessage(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}
