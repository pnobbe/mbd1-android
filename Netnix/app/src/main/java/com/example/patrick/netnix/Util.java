package com.example.patrick.netnix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

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

    /*
* Get a default image from our asset storage.
*/
    public static Bitmap getDefaultImage(Context context) {
        try {
            InputStream ims = context.getAssets().open("images/undefined.png");
            return BitmapFactory.decodeStream(ims);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
