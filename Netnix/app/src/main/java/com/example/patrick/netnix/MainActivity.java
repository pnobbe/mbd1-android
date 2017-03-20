package com.example.patrick.netnix;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

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

    private void makeRequest(final String show) {
        mTextMessage.setText("");
        String url = "http://api.tvmaze.com/search/shows?q=" + Html.escapeHtml(show);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray resp = new JSONArray(response);
                            if (resp.length() > 0) {
                                mTextMessage.setText("Search results for '" + show + "'... \n \n");
                                for(int i = 0; i < resp.length(); i++) {
                                    mTextMessage.append(resp.getJSONObject(i).getJSONObject("show").getString("name") + "\n");
                                }
                            }
                            else {
                                mTextMessage.setText("No shows found under '" + show + "'.");

                            }

                        } catch (final JSONException e) {
                            Log.e("APIWRAPPER", "Json parsing error: " + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("APIWRAPPER", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "An error occurred, please try again later.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        ApiService.getInstance(this).addToRequestQueue(stringRequest);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
