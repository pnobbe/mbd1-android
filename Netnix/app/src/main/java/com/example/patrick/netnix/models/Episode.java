package com.example.patrick.netnix.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object that represents an episode
 */

public class Episode {

    private JSONObject json;
    private JSONObject show;

    public Episode(JSONObject o) {
        this.json = o;

        try {
            this.show = json.getJSONObject("show");
        } catch (JSONException e) {
            this.show = null;
        }
    }

    /*
    * Get the episode ID.
    */
    public String getId(){
        String res = "";
        try {
            res = show.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

    /*
    * Get the show name.
    */
    public String getName(){
        String res = "";
        try {
            res = show.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

    /*
    * Get the URL that points to a representative image for the show. Has to be parsed to Bitmap in order to be displayed.
    */
    public String getImageURL(){
        String res = null;
        try {
            if (!show.isNull("image")) {
                res = show.getJSONObject("image").getString("original");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    /*
    * Get the show status, possible values: "Running", "Ended".
    */
    public String getStatus() {
        String res = "";
        try {
            res = show.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return res;
    }

}
