package com.example.patrick.netnix.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.Html;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

/**
 * Created by Patrick on 3/20/2017.
 */

public class ApiService {
    private final String BASE_URL = "http://api.tvmaze.com/";

    private static ApiService mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private ApiService(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache;

                    {
                        cache = new LruCache<String, Bitmap>(20);
                    }

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

    }

    public static synchronized ApiService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiService(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void getShows(String show, Response.Listener<JSONArray> onResponse, Response.ErrorListener onError) {

        String url = BASE_URL + "search/shows?q=" + Html.escapeHtml(show);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(Request.Method.GET, url, null, onResponse, onError);
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        addToRequestQueue(jsArrRequest);
    }


}
