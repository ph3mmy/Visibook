package com.jcedar.visibook.lautech.ui;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.gcm.RegisterApp;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.ServiceHandler;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentChapterHandler;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentHandler;
import com.jcedar.visibook.lautech.io.model.Student;
import com.jcedar.visibook.lautech.provider.DataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.jcedar.visibook.lautech.helper.AccountUtils.LoadProfileImage;

public class FbActivity extends FragmentActivity {

    private static final String TAG = FbActivity.class.getSimpleName();
    private LoginButton loginBtn;
    private TextView username;
    private ImageView imageView;
    private boolean isEmailChecked = false;
    private Button checkPhoneNumber;
    GoogleCloudMessaging gcm;
    String regid, email;
    ServiceHandler serviceHandler;
    boolean hasGoneToFb = false;
    Context context = FbActivity.this;
    private CallbackManager callbackManager;
    ProgressDialog dialog ;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        if (!AccountUtils.isFirstRun(this)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        serviceHandler = new ServiceHandler();
        setContentView(R.layout.activity_fb);
        dialog = new ProgressDialog(FbActivity.this);

        checkPhoneNumber = (Button) findViewById(R.id.checkPhoneNumber);
        try {
            //For key hashes
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);



        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                    if(newProfile != null){
                        Log.e(TAG+" profile: ", newProfile.getName());
                    }

            }
        };

        profileTracker.startTracking();
        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();

        }

        //loginButton.setReadPermissions(Arrays.asList(new String[]{"public_profile", "email"}));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "confirming in login result");
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                }

            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {

                Log.e(TAG, error.getMessage());
            }
        });



        //checkPhoneNumber.setVisibility(View.GONE);
        checkPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.etPhoneNumber);
                String phone = et.getText().toString().trim();
                /*if (email.toLowerCase().contains("@") && UIUtils.isOnline(FbActivity.this)) {
                    //check db
                    Toast.makeText(FbActivity.this, "Verifying email, please wait", Toast.LENGTH_SHORT).show();
                    new CheckUserEmail(email).execute();
                } else {
                    et.setError("Enter a valid email address");
                }*/
                if( phone.length() > 11){
                    et.setError("Enter a valid phone number");
                }else {
                    new CheckUserEmail(email).execute();
                }
            }
        });

        if (UIUtils.isOnline(this)) {

            // do this if email is checked and found
          /*  loginBtn.setReadPermissions(Arrays.asList("email", "user_friends"));

            loginBtn.registerCallback(new LoginButton.UserInfoChangedCallback() {

                @Override
                public void onUserInfoFetched(GraphUser graphUser) {
                    if (graphUser != null) {


                        AccountUtils.setUserId(FbActivity.this, graphUser.getId());
                        AccountUtils.setUserName(FbActivity.this, graphUser.getName());


                        String photoUrl = "https://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";
                        Log.e(TAG, graphUser.getId() + " get id");
                        hasGoneToFb = true;
                        try {
                            LoadProfileImage ll = new LoadProfileImage();
                            Bitmap bb = ll.execute(photoUrl).get();
                            UIUtils.setProfilePic(FbActivity.this, bb);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        GetAllData allData = new GetAllData();
                        allData.execute();

                       *//* AppHelper.pullAndSaveAllStudentData();
                        AppHelper.pullAndSaveStudentChapterData();*//*

                        startActivity(new Intent(FbActivity.this, DashboardActivity.class));
                        AccountUtils.setFirstRun(false, FbActivity.this);
                        FbActivity.this.finish();
                    }


                }
            });*/

        }

    }

    private void RequestData1() {
        GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e(TAG+" email", object.optString("email"));
                    }
                }).executeAsync();

    }

    public void RequestData(){

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {


                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();

                        try{
                            if(jsonObject != null){
                                Log.e(TAG, jsonObject.toString());
                                Log.e(TAG, object.toString());
                                getFacebookData(jsonObject);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //get users for school
                        GetAllData allData = new GetAllData();
                        allData.execute();

                        startActivity(new Intent(FbActivity.this, DashboardActivity.class));
                        AccountUtils.setFirstRun(false, FbActivity.this);
                        FbActivity.this.finish();
                    }
                });

        Bundle param = new Bundle();
        param.putString("fields", "id, email, name, link, picture");
        request.setParameters(param);
        request.executeAsync();

    }
    public void getFacebookData(JSONObject object) throws JSONException{

            String id = object.getString("id");
            String photoUrl = "https://graph.facebook.com/" + id
                    + "/picture?type=large";

            try {
                LoadProfileImage ll = new LoadProfileImage();
                Bitmap bb = ll.execute(photoUrl).get();
                UIUtils.setProfilePic(FbActivity.this, bb);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            String name;
            if (object.has("first_name") && object.has("last_name")) {
                String fName = object.getString("first_name");
                String lName = object.getString("last_name");

                name = lName + fName;

                AccountUtils.setUserName(FbActivity.this, name);
            }Log.e(TAG, "confirming email");
            if (object.has("email")) {

                String email = object.getString("email");
                Log.e(TAG, "email is: "+email);
                setEmail(email);
                //get user's basic info
                new CheckUserEmail(email).execute();
            }


    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        Log.e(TAG, "email is "+email);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    public class GetAllData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if( dialog != null )
            dialog.setMessage("Getting Required Data....");
        }

        public GetAllData() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";

            StudentChapterHandler ss1 = new StudentChapterHandler(context);
            try {
                String response = ServiceHandler.makeServiceCall
                        (AppSettings.SERVER_URL + "get_all_students.php", ServiceHandler.GET);
                if (response == null) {
                    dialog.dismiss();
                    return null;
                }
                Log.e(TAG, response + " response");

                ArrayList<ContentProviderOperation> operations =
                        new StudentHandler(context).parse(response);
                if (operations.size() > 0) {
                    ContentResolver resolver = context.getContentResolver();
                    resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);
                }
            } catch (IOException | OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }

            try {
                String chapter = "";
                if (AccountUtils.getUserChapter(context) != null) {
                    chapter = AccountUtils.getUserChapter(context);
                }
                Log.e(TAG, "starting student chapter data response");
                String response = ServiceHandler.makeServiceCall
                        (AppSettings.SERVER_URL + "get_user_chapter.php?chapter=" + chapter,
                                ServiceHandler.GET);
                if (response == null) {
                    return null;
                }
                Log.e(TAG, response + " response");

                ArrayList<ContentProviderOperation> operations = ss1.parse(response);
                if (operations.size() > 0) {
                    ContentResolver resolver = context.getContentResolver();
                    resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                }


            } catch (IOException | OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                dialog = null;
            }
        }
    }


    public class CheckUserEmail extends AsyncTask<Void, Void, String> {


        String emailS;

        public CheckUserEmail(String email) {
            emailS = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Verifying email address....");
            dialog.setTitle("Please Wait");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                String url = AppSettings.SERVER_URL + "check_user.php?phone_number=" + emailS;
                result = ServiceHandler.makeServiceCall(url, ServiceHandler.GET);
                Log.e(TAG, result + " json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            Object json;
            JSONObject jsonObject;
            try {
                json = new JSONTokener(jsonString).nextValue();

                if (json instanceof JSONObject) {
                    //you have an object
                    jsonObject = new JSONObject(jsonString);
                    String ss = jsonObject.getString("status");
                    switch (ss) {
                        case "404":
                            Toast.makeText(FbActivity.this,
                                    emailS + " is not found in the database", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                            break;
                        case "101":
                            Toast.makeText(FbActivity.this,
                                   "Enter a valid email address", Toast.LENGTH_SHORT).show();

                            if (dialog.isShowing())
                                dialog.dismiss();
                            break;
                        default:
                            break;
                    }
                    return;
                } else if (json instanceof JSONArray) {
                    //you have an array
                    parseUserJson(jsonString);
                    isEmailChecked = true;
                    //loginBtn.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if ((dialog != null) && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                dialog = null;
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void parseUserJson(String json) {
        if (json == null) {
            return;
        }
        Log.d(TAG, TextUtils.isEmpty(json) ? "Empty Student Json" : json);
        Student[] student = Student.fromJson(json);

        try {
            AccountUtils.setId(this, student[0].getId());
            AccountUtils.setUserGender(this, student[0].getGender());
            AccountUtils.setUserChapter(this, student[0].getChapter());
            AccountUtils.setUserEmail(this, student[0].getEmail());
            AccountUtils.setUserCourse(this, student[0].getCourse());
            AccountUtils.setUserPhoneNumber(this, student[0].getPhoneNumber());
            AccountUtils.setUserDOB(this, student[0].getDateOfBirth());
            Boolean b = Boolean.getBoolean(student[0].getIsAlumni());
            AccountUtils.setIsAlumni(this, b);

            Log.e(TAG, student[0].getChapter() + " chapter");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "inside finally block!!!");
            if (UIUtils.checkPlayServices(this)) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regid = AccountUtils.getRegistrationId(getApplicationContext());
                Log.e(TAG, regid);

                if (regid.isEmpty()) {
                    new RegisterApp(getApplicationContext(), gcm, UIUtils.getAppVersion(getApplicationContext())).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }
        }


    }
}
