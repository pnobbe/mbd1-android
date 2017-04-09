package com.example.patrick.netnix;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.patrick.netnix.models.Show;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class is a utility class for performing the serialization and
 * deserialization operations provided the required information.
 *
 * @author hiteshgarg
 */
public class SerializationUtil {

    /**
     * deserialize to Object from given file. We use the general Object so as
     * that it can work for any Java Class.
     */
    public static Show deserialize(String id, Context mContext) throws IOException,
            ClassNotFoundException {

        SharedPreferences sharedPref = mContext.getSharedPreferences("showData", Context.MODE_PRIVATE);
        String string = sharedPref.getString(id, null);

        if (id != null) {
            byte[] bytes = Base64.decode(string, 0);
            Show object = null;
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                object = (Show) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }
        return null;
    }

    /**
     * serialize the given object and save it to localstorage on given ID.
     */
    public static void serialize(Show s, Context mContext)
            throws IOException {

        SharedPreferences sharedPref = mContext.getSharedPreferences("showData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String encoded = "";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(s);
        objectOutputStream.close();
        encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        editor.putString(s.getId(), encoded);
        editor.apply();
    }

    public static boolean isSerialized(Show s, Context mContext) {
        return mContext.getSharedPreferences("showData", Context.MODE_PRIVATE).contains(s.getId());
    }
}