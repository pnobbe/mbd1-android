package com.example.patrick.netnix.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object that represents a show
 */

public class Show {

    private JSONObject json;
    private JSONObject show;

    public Show(JSONObject o) {
        this.json = o;

        try {
            this.show = json.getJSONObject("show");
        } catch (JSONException e) {
            this.show = null;
        }
    }

    /*
    * Get the show ID.
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

    /*
    * Get the show rating.
    */
    public float getRating(){
        int res = 0;
        try {
            if (!show.isNull("rating")) {
                res = show.getJSONObject("rating").getInt("average");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
        return (float) res / 2;
    }

    /*
    * Get the show summary.
    */
    public String getSummary() {
        String res = "";
        try {
            res = show.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
            return "No description available.";
        }
        return res;
    }


}
