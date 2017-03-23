package com.example.patrick.netnix;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Patrick on 3/22/2017.
 */

public class Util {

    /*
    * Lazy error handler
    */
    public static void showError(final Exception message, final Activity act) {
        Log.e("Volley Error", message.getMessage());
        message.printStackTrace();
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act.getApplicationContext(),
                        message.getMessage(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}
