package com.jcedar.visibook.lautech.helper;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;


public class AccountUtils {
    private static final String TAG = AccountUtils.class.toString();

    private static final String PREF_CHOSEN_ACCOUNT = "chosen_account";
//    private static final String PREF_LOGGED_OUT_ACCOUNT = "logged_out_account";
    private static final String PREF_AUTH_TOKEN = "auth_token";
    private static final String PREF_FIRST_RUN = "firstRun";
    private static final String PREF_SETUP_DONE = "done_setup";

    public static final String PREF_ORGANIZATION = "organisation";
    public static final String PREF_FULLNAME = "fullName";
    public static final String PREF_REG_ID = "regId";

//    public static final String PREF_ROLE_ID = "roleId";
    public static final String PREF_ROLE = "roleName";
    private static final String PREF_GENDER = "gender";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_COURSE = "course";
    private static final String PREF_CHAPTER = "chapter";
    private static final String PREF_PHONE_NUMBER = "phone_number";
    private static final String PREF_DOB = "date_of_birth";
    private static final String PREF_ALUMNI = "isAlumni";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PREF_SERVER_ID = "server_id";

    String hash = "+MpC+7H4vWDozl9v3x+c26fTirY=";
    private static final String PREF_NAME = "name";
    private static final String PREF_ID = "_id";


    public static String getChosenAccountName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CHOSEN_ACCOUNT, null);
    }

    public static void setAuthToken(final Context context, final String authToken) {
        Log.i(TAG, "Auth token of length "
                + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_AUTH_TOKEN, authToken).apply();
        Log.d(TAG, "Auth Token: " + authToken);
    }


    public static void setFullName(final Context context, final String fullName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_FULLNAME, fullName).apply();
    }

    public static String getFullName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_FULLNAME, null);
    }

    public static void setRegId(final Context context, final String regid){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_REG_ID, regid).apply();
    }

    public static void setRole(final Context context, final String roleName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_ROLE, roleName).apply();
    }

    public static String getRole(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ROLE, null);
    }
    //  create get and set for ParticipantType and MarketParticipantId

    public static void setFirstRun(final boolean isFirst, final Context context){

        Log.d(TAG, "Set first run to" + Boolean.toString(isFirst));
        //SharedPreferences sp = context.getSharedPreferences(PREF_ACCOUNT, Context.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_FIRST_RUN, isFirst).apply();
    }

    public static boolean isFirstRun(final Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return  sp.getBoolean(PREF_FIRST_RUN, true);
    }

    public static Account getChosenAccount(final Context context) {
        String account = getChosenAccountName(context);
        if (account != null) {
            return new Account(account, context.getString(R.string.account_type));
        } else {
            return null;
        }
    }


    // If syncing return true
    public static boolean signOut(Context context){

        //Remove reg_id from server( push userId)
        //delete content provider
        //set all shared preference to null or delete

        //AccountUtils.setLoggedAccountName(context, getChosenAccountName(context));
        Account account = AccountUtils.getChosenAccount(context);
        if (account == null) return false;

        boolean syncActive = ContentResolver.isSyncActive(
                account, DataContract.CONTENT_AUTHORITY);
        boolean syncPending = ContentResolver.isSyncPending(
                account, DataContract.CONTENT_AUTHORITY);

        //Cancel sync
        boolean syncing = syncActive || syncPending;
        if(syncing){
            ContentResolver.cancelSync(account, DataContract.CONTENT_AUTHORITY);
            return true;
        }

        //Delete all server registration
        // 1a. GCM


        // 1b. Stop sync
        ContentResolver.setIsSyncable(account, DataContract.CONTENT_AUTHORITY, 0);

        // 2a. Invalidate token
        invalidateAuthToken(context);

        // 2b. Remove Account
       // SessionManager.removeAccount(account, context);

        // 3. Delete prefs data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences preferences = context.getSharedPreferences(PREF_ACCOUNT, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        // 4. Delete local data
        context.getContentResolver().delete(DataContract.BASE_CONTENT_URI, null, null);
        return false;

    }



    private static void invalidateAuthToken(Context context) {
        //SessionManager.invalidateAuthToken(context, getAuthToken(context));
        setAuthToken(context, null);
    }

    public static void setUserName(final Context context, final String name){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_NAME, name).commit();
    }

    public static String getUserName(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_NAME, null);
    }

    public static void setUserId(final Context context, final String userId){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_ID, userId).commit();
    }

    public static String getUserId(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ID, null);
    }

    public static void setId(final Context context, final String userId){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString(PREF_SERVER_ID, userId).commit();
        }

        public static String getId(final Context context) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(PREF_SERVER_ID, null);
        }



    public static void setUserGender(final Context context, final String gender){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_GENDER, gender).commit();
    }

    public static String getUserGender(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_GENDER, null);
    }


    public static void setUserChapter(final Context context, final String chapter){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_CHAPTER, chapter).apply();
    }

    public static String getUserChapter(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CHAPTER, null);
    }


    public static void setUserEmail(final Context context, final String email){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_EMAIL, email).apply();
    }

    public static String getUserEmail(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_EMAIL, null);
    }

    public static void setUserCourse(final Context context, final String course){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_COURSE, course).apply();
    }

    public static String getUserCourse(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_COURSE, null);
    }

    public static void setUserPhoneNumber(final Context context, final String phone){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PHONE_NUMBER, phone).apply();
    }

    public static String getUserPhoneNumber(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PHONE_NUMBER, null);
    }

    public static void setIsAlumni(final Context context, final boolean isAlumni){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_ALUMNI, isAlumni).apply();
    }

    public static boolean isAlumni(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_ALUMNI, false);
    }



    public static void setUserDOB(final Context context, final String dob){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_DOB, dob).apply();
    }

    public static String getUserDOB(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_DOB, null);
    }

    private static final String PREF_PHONE_NUMBER_1 = "phone_number1";

    public static void setPhoneNumber(final Context context, final String phoneNumber){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_PHONE_NUMBER_1, phoneNumber).apply();
    }

    public static String getPhoneNumber(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_PHONE_NUMBER_1, "");
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Log.d(TAG, "registration id == "+registrationId);

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = UIUtils.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }















    public byte[] setPicture(String fullPhotoUrl) {
        byte[] picByte= null;
        Bitmap bb=null;
        try {
            bb = new LoadProfileImage().execute(fullPhotoUrl).get();
            picByte = getBytes(bb);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return picByte;
    }
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


    public static class LoadProfileImage extends AsyncTask<String, String, Bitmap> {
        // ImageView downloadedImage;
        Bitmap photoBitmap;

        public LoadProfileImage() {
            //this.downloadedImage = image;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage() + "Hello world");
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            //downloadedImage.setImageBitmap(result);
            photoBitmap = result;
        }

    }


}
