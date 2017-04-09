package com.example.patrick.netnix.models;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Object that represents an episode.
 */

public class Episode implements Serializable {

    /**
     * Generated Serial version Id
     */
    private static final long serialVersionUID = -55857686305273841L;

    private transient JSONObject episode;
    private boolean isWatched;
    private String parentShowName;

    public Episode(String parentShowName, JSONObject e) {
        this.episode = e;
        this.parentShowName = parentShowName;
        this.isWatched = false;
    }

    /**
     * Returns the root JSON object.
     */
    public JSONObject getJSON() {
        return this.episode;
    }

    /**
     * Sets the root JSON object.
     */
    public void setJSON(JSONObject o) {
        this.episode = o;
    }

    /**
     * Get the episode ID.
     */
    public String getId() {
        try {
            if (!episode.isNull("id")) {
                return episode.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the episode number.
     */
    public int getNumber() {
        try {
            if (!episode.isNull("number")) {
                return episode.getInt("number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the season number.
     */
    public int getSeasonNumber() {
        try {
            if (!episode.isNull("season")) {
                return episode.getInt("season");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the episode name.
     */
    public String getName() {
        try {
            if (!episode.isNull("name")) {
                return episode.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the parent show name.
     */
    public String getShowName() {
        return parentShowName;
    }


    /**
     * Get the airdate for this episode.
     */
    public Calendar getAirDate() {
        try {
            if (!episode.isNull("airdate") && !episode.isNull("airtime")) {

                String airdate = episode.getString("airdate");
                String airtime = episode.getString("airtime");
                String res = (airdate + ((airtime == null) ? " 00:00" : " " + airtime));
                Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(res);
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                return c;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Get the runtime for this episode in minutes.
     */
    public int getRuntime() {
        try {
            if (!episode.isNull("runtime")) {
                return episode.getInt("runtime");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Default to 1 hour, not always accurate but that isn't the end of the world in this case.
        return 60;
    }


    public boolean isWatched() {
        return this.isWatched;
    }

    public void setIsWatched(boolean isWatched) {
        this.isWatched = isWatched;
    }

    public void addToCalendar(Activity a) {

        // Set event properties
        long beginTime = getAirDate().getTimeInMillis();
        long endTime = beginTime + getRuntime() * 60 * 1000;
        String title = getShowName() + " Season " + getSeasonNumber() + " Episode " + getNumber();

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", beginTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("title", title);
        a.startActivity(intent);

    }

    public void flipIsWatched() {
        this.isWatched = !this.isWatched;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(episode.toString());
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        episode = new JSONObject((String) ois.readObject());
    }

}
