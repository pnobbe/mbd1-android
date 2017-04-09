package com.example.patrick.netnix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Global utility class
 */

public class Util {

    /**
     * Lazy error handler
     * @param message   -   Exception that is thrown, containing the relevant message
     * @param act       -   Activity that will execute the event
     */
    public static void showError(final Exception message, final Activity act) {
        if (message != null) {
            Log.e("ERROR", message.getMessage() + "");
            message.printStackTrace();
            if (act != null) {
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
    }

    /**
     * Lazy toast handler
     * @param message   -   String that will be shown to the user
     * @param act       -   Activity that will execute the event
     */
    public static void showToast(final String message, final Activity act) {
        if (message != null && act != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act.getApplicationContext(), message,
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

    }

    /**
     * Gets a default image from the assets as Bitmap.
     * @param context   -   Context
     * @return          -   Bitmap containing the default image. Null in case of an exception
     */
    public static Bitmap getDefaultImage(Context context) {
        try {
            InputStream ims = context.getAssets().open("images/no_image.png");
            return BitmapFactory.decodeStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if the first calendar date is equal to the second calendar date ignoring time.
     *
     * @param cal1 the first calendar, not altered, not null.
     * @param cal2 the second calendar, not altered, not null.
     * @return true if cal1 date is equal to the cal2 date ignoring time.
     * @throws IllegalArgumentException if either of the calendars are null
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Checks if a calendar date is after today and within a number of days in the future.
     *
     * @param cal  the calendar, not altered, not null
     * @param days the number of days.
     * @return true if the calendar date day is after today and within days in the future .
     * @throws IllegalArgumentException if the calendar is null
     */
    public static boolean isWithinDaysFuture(Calendar cal, int days) {
        if (cal == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar today = Calendar.getInstance();
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_YEAR, days);
        return (isAfterDay(cal, today) && !isAfterDay(cal, future));
    }

    /**
     * Checks if the first calendar date is after the second calendar date ignoring time.
     *
     * @param cal1 the first calendar, not altered, not null.
     * @param cal2 the second calendar, not altered, not null.
     * @return true if cal1 date is after cal2 date ignoring time.
     * @throws IllegalArgumentException if either of the calendars are null
     */
    public static boolean isAfterDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return false;
        if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return true;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return false;
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return true;
        return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
    }
}
