package com.jcedar.visibook.lautech.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Afolayan on 22/1/2016.
 */
public class PrefUtils {
    public static  final String SOUND_KEY = "sounds";
    public static  final String VIBRATE_KEY = "vibration";
    public static  final String NOTIFY_KEY = "notification";
    public static  final String PERSON_KEY = "personal_key";
    public static  final String EMAIL_KEY = "email_key";
    public static  final String ALIAS_KEY = "alias_key";
    public static  final String PHONE_KEY = "phone_key";
    public static  final String PHOTO_KEY = "photo_key";

    public static boolean hasSound(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SOUND_KEY, true); // by default we want sound
    }

    public static boolean hasVibration(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(VIBRATE_KEY, true); // by default we want vibrations
    }

    public static boolean hasNotification(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(NOTIFY_KEY, true); // by default we are notified
    }
    public static void setPersonKey(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PERSON_KEY, key).commit();
    }

    public static String getPersonal(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PERSON_KEY, "0");
    }

    public static void setEmail(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(EMAIL_KEY, key).commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(EMAIL_KEY, "0");
    }




    public static void setPhoto(Context context, Bitmap image){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PHOTO_KEY, encodeTobase64(image)).commit();
    }

    public static String getPhoto(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PHOTO_KEY, "0");
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
