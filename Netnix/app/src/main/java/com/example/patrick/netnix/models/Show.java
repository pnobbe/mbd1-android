package com.example.patrick.netnix.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.patrick.netnix.SerializationUtil;
import com.example.patrick.netnix.fragments.AsyncListener;
import com.example.patrick.netnix.Cache;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.services.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Object that represents a show.
 */

public class Show implements Parcelable, Serializable {

    /**
     * Generated Serial version Id
     */
    private static final long serialVersionUID = -55857686305273843L;

    private transient JSONObject show;
    private ArrayList<Season> seasons;

    public Show(JSONObject s) {
        this.show = s;
        this.seasons = new ArrayList<>();
    }

    /**
     * Returns the root JSON object.
     */
    public JSONObject getJSON() {
        return this.show;
    }

    /**
     * Sets the root JSON object.
     */
    public void setJSON(JSONObject o) {
        this.show = o;
    }

    /**
     * Get the show ID.
     */
    public String getId() {
        try {
            if (!show.isNull("id")) {
                return show.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the show name.
     */
    public String getName() {
        try {
            if (!show.isNull("name")) {
                return show.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the URL that points to a representative image for the show. Has to be parsed to Bitmap in order to be displayed.
     */
    public String getImageURL() {
        try {
            if (!show.isNull("image")) {
                return show.getJSONObject("image").getString("original");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the URL that points to a representative image for the show. Has to be parsed to Bitmap in order to be displayed.
     */
    public String getNextEpisodeURL() {
        try {
            if (!show.isNull("_links")) {
                if (show.getJSONObject("_links").has("nextepisode")) {
                    return show.getJSONObject("_links").getJSONObject("nextepisode").getString("href");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the show status, possible values: "Running", "Ended".
     */
    public String getStatus() {
        try {
            if (!show.isNull("status")) {
                return show.getString("status");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the show rating.
     */
    public float getRating() {
        try {
            if (!show.isNull("rating")) {
                return (float) show.getJSONObject("rating").getInt("average") / 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the show summary.
     */
    public String getSummary() {
        try {
            if (!show.isNull("summary")) {
                return show.getString("summary");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the network name.
     */
    public String getNetwork() {
        try {
            if (!show.isNull("network")) {
                return show.getJSONObject("network").getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get genres as a single string delimited with a comma.
     */
    public String getGenres() {
        try {
            if (!show.isNull("genres")) {
                JSONArray g = show.getJSONArray("genres");
                if (g.length() > 0) {
                    String res = "";
                    for (int i = 0; i < g.length(); i++) {
                        if (i != 0) {
                            res += ", ";
                        }
                        res += g.getString(i);
                    }
                    return res;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a Calendar object containing the timestamp the show was last updated.
     */
    public Calendar getUpdated() {
        try {
            if (!show.isNull("updated")) {
                Date d = new Date(show.getLong("updated") * 1000);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                return c;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the total amount of episodes that have aired. (Sum of all episodes in each individual season)
     */
    public int getTotalEpisodes() {
        int i = 0;
        for (Season s : getSeasons()) {
            i += s.getTotalEpisodes();
        }
        return i;
    }

    /**
     * Get the total amount of episodes that have aired and the user has watched. (Sum of all episodes in each individual season)
     */
    public int getWatchedEpisodes() {
        int i = 0;
        for (Season s : getSeasons()) {
            i += s.getWatchedEpisodes();
        }
        return i;
    }

    /**
     * Get next episode that is due to air (if show has ended, return null)
     */
    public void getNextEpisode(Context mContext, final Activity mActivity, final AsyncListener listener) {
        if (getNextEpisodeURL() != null) {
            ApiService.getInstance(mContext).getEpisode(getNextEpisodeURL(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            listener.callback(new Episode(getName(), response));
                        }
                    } catch (final Exception e) {
                        Util.showError(e, mActivity);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    Util.showError(e, mActivity);
                }
            });
        }
    }

    public ArrayList<Season> getSeasons() {
        return this.seasons;
    }


    /**
     * Follow/unfollow part
     */

    /**
     * Check if the show is followed
     */
    public boolean isFollowed(Context mContext) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("followedShows", Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getId(), false);
    }

    /**
     * Follow the show
     */
    public void follow(Activity mActivity, Context mContext, AsyncListener listener) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("followedShows", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Flag boolean as true on the local storage position equal to this show's ID.
        editor.putBoolean(getId(), true);
        editor.apply();

        // Retrieve all seasons and episodes from the API.
        // We don't add the show to the cache or localstorage yet because the data would be incomplete.
        // Let the AsyncListener's callback handle this.
        getSeasonsFromApi(mActivity, mContext, listener);
    }

    /**
     * Unfollow the show
     */
    public void unfollow(Activity mActivity, Context mContext) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("followedShows", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Flag boolean as false on the local storage position equal to this show's ID.
        // We use a separate flag to preserve episode data in case the user wants to refollow the show and not lose data.
        editor.putBoolean(getId(), false);
        editor.apply();

        // Remove show from cache
        Cache c = ((Cache) mActivity.getApplication());
        c.getMyShows().remove(c.getMyShows().indexOf(this));
    }


    /**
     * Update the show
     */
    public void update(Show update) {

        // Loop through the show's seasons/episodes to carry over the isWatched properties of the episodes.
        for (int i = 0; i < update.getSeasons().size(); i++) {

            // Check if the old show has the season
            if (getSeasons().size()-1 > i) {

                // If so, check the individual episodes.
                Season curSeason = getSeasons().get(i);
                Season updatedSeason = update.getSeasons().get(i);
                for (int j = 0; j < updatedSeason.getEpisodes().size(); j++) {

                    // Check if the old season has the episode
                    if (curSeason.getEpisodes().size()-1 > j) {

                        // If so, carry over the isWatched property.
                        updatedSeason.getEpisodes().get(j).setIsWatched(curSeason.getEpisodes().get(j).isWatched());
                    }
                }
            }
        }

        // Replace the old show entirely with the new one. Quicker than updating the old to fit the new.
        this.seasons = update.getSeasons();
        setJSON(update.getJSON());
    }


    /**
     * Gets all seasons from the API that have aired
     */
    public void getSeasonsFromApi(final Activity mActivity, final Context mContext, final AsyncListener listener) {
        this.seasons = new ArrayList<>();

        ApiService.getInstance(mContext).getSeasons(getId(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {

                        // Prepare the season objects to be populated with episodes.
                        for (int i = 0; i < response.length(); i++) {
                            Season s = new Season(response.getJSONObject(i));

                            // Only prepare a season object if it has aired already.
                            if (s.getPremiereDate() != null) {
                                Calendar today = Calendar.getInstance();
                                today.set(Calendar.HOUR_OF_DAY, 0);
                                if (!Util.isAfterDay(s.getPremiereDate(), today)) {
                                    seasons.add(s);
                                }
                            }
                        }

                        // Season objects are prepared, populate them with episodes.
                        getEpisodesFromApi(mActivity, mContext, listener);
                    }
                } catch (final Exception e) {
                    Util.showError(e, mActivity);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Util.showError(e, mActivity);
            }
        });
    }

    /**
     * Get all episodes from the API and populates the appropriate season object.
     */
    private void getEpisodesFromApi(final Activity mActivity, final Context mContext, final AsyncListener listener) {
        final Show self = this;

        ApiService.getInstance(mContext).getEpisodes(getId(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);

                        // Simulate storing the episodes in seasons so we can do this in one operation later.
                        HashMap<String, ArrayList<Episode>> episodes = new HashMap<>();
                        for (int i = 0; i < response.length(); i++) {
                            Episode episode = new Episode(getName(), response.getJSONObject(i));
                            if (episode.getAirDate() != null) {

                                // Only process the episode if it has aired.
                                if (!Util.isAfterDay(episode.getAirDate(), today)) {
                                    String season = episode.getSeasonNumber() + "";
                                    if (!episodes.containsKey(season)) {
                                        episodes.put(season, new ArrayList<Episode>());
                                    }
                                    episodes.get(season).add(episode);
                                }
                            }
                        }

                        // Store episodes in appropriate season object.
                        for (String key : episodes.keySet()) {
                            if (seasons.size() > Integer.parseInt(key) - 1) {
                                seasons.get(Integer.parseInt(key) - 1).setEpisodes(episodes.get(key));
                            }
                        }
                        listener.callback(self);
                    }
                } catch (final Exception e) {
                    Util.showError(e, mActivity);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Util.showError(e, mActivity);
            }
        });
    }

    /**
     * Parcellable implementation
     */
    public Show(Parcel in) throws JSONException {
        this.show = new JSONObject(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(show.toString());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Show createFromParcel(Parcel in) {
            try {
                return new Show(new JSONObject(in.readString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Show[] newArray(int size) {
            return new Show[size];
        }
    };


    /**
     * Serializable implementation
     */
    public void saveToSharedPreference(Context mContext) {
        try {
            SerializationUtil.serialize(this, mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(show.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        show = new JSONObject((String) ois.readObject());
    }
}
