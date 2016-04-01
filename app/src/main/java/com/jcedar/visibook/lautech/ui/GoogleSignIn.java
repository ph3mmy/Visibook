package com.jcedar.visibook.lautech.ui;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visibook.lautech.gcm.RegisterApp;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.helper.ServiceHandler;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentChapterHandler;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentHandler;
import com.jcedar.visibook.lautech.io.model.Student;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by oluwafemi.bamisaye on 3/26/2016.
 */
public class GoogleSignIn extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {



    private static final String TAG = GoogleSignIn.class.getSimpleName();
    private Button checkPhoneNumber;
    private SignInButton btnSignIn;
//    CircularProgressButton btEnter;
    private static int RC_SIGN_IN = 0;
    private boolean isEmailChecked = false;
    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regid, email;
    Context context = GoogleSignIn.this;


    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    String personPhotoUrl,personName;
    static Bitmap bResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AccountUtils.isFirstRun(this)) {
            startActivity(new Intent(this, NewDashBoardActivity.class));
            finish();
        }
        setContentView(R.layout.activity_google_signin);



        dialog = new ProgressDialog(GoogleSignIn.this);
        checkPhoneNumber = (Button) findViewById(R.id.checkPhoneNumber);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);


        btnSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]




        //checkPhoneNumber.setVisibility(View.GONE);
        checkPhoneNumber.setOnClickListener(this); /*{
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.etPhoneNumber);
                String phone = et.getText().toString().trim();
                *//*if (email.toLowerCase().contains("@") && UIUtils.isOnline(FbActivity.this)) {
                    //check db
                    Toast.makeText(FbActivity.this, "Verifying email, please wait", Toast.LENGTH_SHORT).show();
                    new CheckUserEmail(email).execute();
                } else {
                    et.setError("Enter a valid email address");
                }*//*
                if( phone.length() > 11){
                    et.setError("Enter a valid phone number");
                }else {
                    new CheckUserEmail(email).execute();
                }
            }
        });*/


    }

    @Override
    public void onStart() {
        super.onStart();


        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(TAG, "Got cached sign-in");/*
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);*/

            Intent dashbIntent = new Intent(this, NewDashBoardActivity.class);
            dashbIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashbIntent);

        } else {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("TEST", " is result code 0?: " + resultCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
//        Toast.makeText(this, "An Error Occured, Check your Network Connection Settings",Toast.LENGTH_SHORT).show();
//        btEnter.setVisibility(View.GONE);
//        btnSignIn.setVisibility(View.VISIBLE);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String emailPref = acct.getEmail();

            Log.d(TAG, " Handle signIn email of user" + emailPref);
            PrefUtils.setEmail(this, emailPref);
            //save Acct Name
            String namePref = acct.getDisplayName();
            PrefUtils.setPersonKey(this, namePref);


            Uri mPhoto = acct.getPhotoUrl();
            if (mPhoto != null) {

//                personPhotoUrl = acct.getPhotoUrl().toString();
                personPhotoUrl = mPhoto.toString();
                Log.e(TAG, "Profile Image" + personPhotoUrl);
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage().execute(personPhotoUrl);
            }else

            {
                Bitmap def = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                PrefUtils.setPhoto(this, def);
                UIUtils.setProfilePic(this, def);

                Log.e(TAG, "Converted Launcher ICOn   " + def);

                Toast.makeText(this, "Profile Loaded, Registering User", Toast.LENGTH_SHORT).show();

                enterDashBoard();
            }

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Ensure you are connected to a Network to be able to Sign In",Toast.LENGTH_SHORT).show();
//            signOutUser();
            RC_SIGN_IN++;
            if (dialog != null){
                dialog.dismiss();
            }
            btnSignIn.setVisibility(View.VISIBLE);
//            btEnter.setVisibility(View.GONE);
            /*signOutUser();
            Toast.makeText(this, "An Error Occured, Check your Network Connection Settings",Toast.LENGTH_SHORT).show();
            btEnter.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);*/
//            updateUI(false);
        }
    }
    // [END handleSignInResult]

//    public void signOutUser()
//    {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        Snackbar.make(snackView, "Enter your details and sign in",Snackbar.LENGTH_LONG).show();
//                    }
//                });
//    }

    private void signOutUser() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
        btnSignIn.setVisibility(View.VISIBLE);
    }
    // [END signOut]

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage = (ImageView) findViewById(R.id.imgProfilePicNull);
        Bitmap bitmap = GoogleSignIn.bResult;

        public LoadProfileImage() {
//            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            btEnter.setVisibility(View.VISIBLE);
            btEnter.setIndeterminateProgressMode(true);
            btEnter.setProgress(50);*/

            if( dialog != null )
                dialog.setMessage("Getting Required Data....");
//            Toast.makeText(GoogleSignIn.this, "Please Wait.....", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
//            hidepDialog();
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }


            Log.e(TAG, "Bitmap returned" + mIcon11);
            return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {
            this.bitmap=result;

            Log.e(TAG, "ONPOSTEXECUTE result    " +result);
//            bmImage.setImageBitmap(result);
//            buttonContainer.setBackgroundResource(R.drawable.transparent_button);
//            btnSignIn.setProgress(0);
//            Toast.makeText(Login.this,"Profile Loaded", Toast.LENGTH_SHORT).show();
            if (result != null){
                PrefUtils.setPhoto(GoogleSignIn.this, result);
                UIUtils.setProfilePic(GoogleSignIn.this, result);
                String m = PrefUtils.encodeTobase64(result);
                Log.e(TAG, "Encoded Profile image " + m);


                enterDashBoard();
            }else {
                /*Bitmap def = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                PrefUtils.setPhoto(context,def);

                Log.e(TAG, "Converted Launcher ICOn   " + def);
                Toast.makeText(Login.this, "Invalid image", Toast.LENGTH_SHORT).show();*/
            }

//            registerUser();
        }
    }

    // [START signIn]
    private void signIn() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No Internet Connectivity detected! Check your Internet Connectivity settings")
                    .setCancelable(false)
                    .setPositiveButton("Check Settings", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return;

        }else

            startActivityForResult(signInIntent, RC_SIGN_IN);
        btnSignIn.setVisibility(View.GONE);
//        showpDialog();


        dialog.setMessage("Signing In....");
        dialog.setTitle("Please Wait");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();

//        Toast.makeText(this, "Signing In" ,  Toast.LENGTH_SHORT).show();
    }
    // [END signIn]


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
                            Toast.makeText(GoogleSignIn.this,
                                    emailS + " is not found in the database", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                            break;
                        case "101":
                            Toast.makeText(GoogleSignIn.this,
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


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    public void enterDashBoard() {
        startActivity(new Intent(GoogleSignIn.this, NewDashBoardActivity.class));
        AccountUtils.setFirstRun(false, GoogleSignIn.this);
        GoogleSignIn.this.finish();
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.btn_sign_in:
            signIn();
            break;
        case R.id.checkPhoneNumber:
            EditText et = (EditText) findViewById(R.id.etPhoneNumber);
            String phone = et.getText().toString().trim();
        if( phone.length() > 11){
            et.setError("Enter a valid phone number");
        }else {
            new CheckUserEmail(email).execute();
        }
    }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

/*
    private void showpDialog() {
        if (btEnter.getProgress() == 0) {
            btEnter.setVisibility(View.VISIBLE);
            btEnter.setProgress(50);
        }
    }

    private void hidepDialog() {
        if (btEnter.getProgress() > 0) {
            btEnter.setProgress(0);
            btEnter.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }*/
}
