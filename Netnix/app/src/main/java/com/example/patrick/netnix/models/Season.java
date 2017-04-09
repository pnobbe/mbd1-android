package com.example.patrick.netnix.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Object that represents a season.
 */

public class Season implements Serializable {

    /**
     * Generated Serial version Id
     */
    private static final long serialVersionUID = -55857686305273842L;

    private transient JSONObject season;
    private ArrayList<Episode> episodes;

    public Season(JSONObject s) {
        this.season = s;
        this.episodes = new ArrayList<>();
    }

    /**
     * Returns the root JSON object.
     */
    public JSONObject getJSON() {
        return this.season;
    }

    /**
     * Sets the root JSON object.
     */
    public void setJSON(JSONObject o) {
        this.season = o;
    }

    /**
     * Get the season ID.
     */
    public String getId() {
        try {
            if (!season.isNull("id")) {
                return season.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the season number.
     */
    public int getNumber() {
        try {
            if (!season.isNull("number")) {
                return season.getInt("number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the show name.
     */
    public String getName() {
        try {
            if (!season.isNull("name")) {
                return season.getString("name");
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
            if (!season.isNull("image")) {
                return season.getJSONObject("image").getString("original");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the airdate for this episode.
     */
    public Calendar getPremiereDate() {
        if (!season.isNull("premiereDate")) {
            try {
                String premieredate = season.getString("premiereDate");
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(premieredate);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                return c;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public int getTotalEpisodes() {
        return getEpisodes().size();
    }


    public int getWatchedEpisodes() {
        int i = 0;
        for (Episode e : getEpisodes()) {
            if (e.isWatched()) {
                i++;
            }
        }
        return i;
    }

    public boolean isWatched() {
        for (Episode e : getEpisodes()) {
            if (!e.isWatched()) {
                return false;
            }
        }
        return true;
    }

    public void flipAllWatched() {
        boolean areAllWatched = isWatched();
        for (Episode e : getEpisodes()) {
            e.setIsWatched(!areAllWatched);
        }
    }

    public void setEpisodes(ArrayList<Episode> e) {
        this.episodes = e;
    }

    /**
     * Get all episodes.
     */
    public ArrayList<Episode> getEpisodes() {
        return this.episodes;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(season.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        season = new JSONObject((String) ois.readObject());
    }

}
