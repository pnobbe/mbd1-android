package com.example.patrick.netnix;

import android.app.Application;
import android.content.Context;

import com.example.patrick.netnix.models.Show;

import java.util.ArrayList;
import java.util.Map;

/**
 * Used for caching show data.
 */

public class Cache extends Application {

    private ArrayList<Show> myShows = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // Fill cache with localstorage data.
        Map<String, ?> allEntries = getApplicationContext().getSharedPreferences("followedShows", Context.MODE_PRIVATE).getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if ((Boolean) entry.getValue()) {
                try {
                    Show s = SerializationUtil.deserialize(entry.getKey(), getApplicationContext());
                    if (s != null) {
                        myShows.add(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Show> getMyShows() {
        return this.myShows;
    }

}
