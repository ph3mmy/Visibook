package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.MultipartEntity;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.ui.view.Fab;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.jcedar.visibook.lautech.helper.AccountUtils.getFullName;
import static com.jcedar.visibook.lautech.helper.AccountUtils.getUserName;
import static com.jcedar.visibook.lautech.helper.PrefUtils.getPhoto;

/**
 * Created by Seyi.Afolayan on 4/7/2016.
 */
public class ProfileFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_FROM_FILE = 200;
    private static final int REQUEST_FROM_FB = 300;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Visibook";
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView fullName, emailView, dobView, courseView, numberView;
    private Context context;
    private Listener mCallback;

    private Fab fab1;
    private MaterialSheetFab<Fab> materialSheetFab;

    LinearLayout changeImage;
    LinearLayout changeProfile;
    Uri selectedImageUri;
    private ViewGroup container;
    private String email, dob, course, phoneNumber, name;
    private AlertDialog.Builder builder;
    private RelativeLayout alertView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_user_profile, container, false);

        this.container = container;
        context = getActivity();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(AccountUtils.getUserName(context));
        toolbar.setOnMenuItemClickListener(this);
        //setSupportActionBar(toolbar);

        imageView = (ImageView) view.findViewById(R.id.my_profile_image);
        fullName = (TextView) view.findViewById(R.id.fullNameProfile);
        emailView = (TextView) view.findViewById(R.id.tvEmailProfile);
        dobView = (TextView ) view.findViewById(R.id.tvDateOfBirthProfile);
        courseView = (TextView) view.findViewById(R.id.tvCourseSchool);
        numberView = (TextView) view.findViewById(R.id.tvPhoneNumberProfile);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        toolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(context, PicViewActivity.class));
                return false;
            }
        });
        Drawable d = new BitmapDrawable(getResources(), getPhoto(context));

        toolbarLayout.setStatusBarScrim(d);
        toolbarLayout.setStatusBarScrimColor(UIUtils.getDominantColor(getPhoto(context)));

        imageView.setImageBitmap(getPhoto(context));

        name = AccountUtils.getUserName(context);
        fullName.setText(name);
        email = AccountUtils.getUserEmail(context);
        dob = AccountUtils.getUserDOB(context);
        course = AccountUtils.getUserCourse(context);
        phoneNumber = AccountUtils.getUserPhoneNumber(context);


        emailView.setText( isNotNull(email));
        dobView.setText( isNotNull(dob));



        courseView.setText( isNotNull(course) );
        numberView.setText(isNotNull(phoneNumber));

        //getToolbar().setVisibility(View.GONE);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabEditProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = AccountUtils.getId(getActivity());
                UpdateFragment updateFragment = UpdateFragment.newInstance(Long.parseLong(id));
                updateFragment.show(getFragmentManager(), "User");
            }
        });
        fab.setVisibility(View.GONE);

        fab1 = (Fab) view.findViewById(R.id.fab);
        View sheetView = view.findViewById( R.id.fab_sheet);
        View overlay = view.findViewById( R.id.overlay);

        int sheetColor = getResources().getColor(R.color.sim_grey);
        int fabColor = getResources().getColor(R.color.theme_accent_1);

        materialSheetFab = new MaterialSheetFab<>(fab1, sheetView, overlay, sheetColor, fabColor);

        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
            }

            @Override
            public void onSheetHidden() {
                super.onSheetHidden();
            }
        });

        view.findViewById(R.id.change_image_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image choose dialog
                Log.e(TAG, "inside change image layout");
                fab1.hide();
                openImageIntent();
            }
        });
        view.findViewById(R.id.edit_profile_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab1.hide();
                String id = AccountUtils.getId(getActivity());
                UpdateFragment updateFragment = UpdateFragment.newInstance(Long.parseLong(id));
                updateFragment.show(getFragmentManager(), "User");
            }
        });

        alertView = (RelativeLayout) view.findViewById(R.id.layout_alert);
        return view;
    }



    private void openImageIntent() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.layout_choose_image, container, false);
        view.findViewById(R.id.chooseCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);*/
                captureImage();
            }
        });

        view.findViewById(R.id.chooseFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertView.setVisibility(View.GONE);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_FROM_FILE);
            }
        });

        view.findViewById(R.id.chooseFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo : Facebook login
                //Todo: get picture from user's profile
                alertView.setVisibility(View.GONE);
            }
        });


        builder = new AlertDialog.Builder(getActivity()).setCancelable(true);
        builder.create();
        builder.setView(view)
                .setTitle("Complete action using");

        builder.show();
    }

    private  String getUsername(){
        return getFullName(getActivity()).replace(" ", "_");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("file_uri", selectedImageUri);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            selectedImageUri = savedInstanceState.getParcelable("file_uri");
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        selectedImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            String path = selectedImageUri.getPath();
            if( path != null){
                previewImage(path);
            }

            //upload pix

        }
    }

    private void previewImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        imageView.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to save this image?")
                .setNeutralButton("Try another", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureImage();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtils.setPhoto(getActivity(), bitmap);
                        new UploadTask().execute(bitmap);
                    }
                })
                .create();

        builder.show();



    }




    private static String isNotNull(String string){
        return string != null ? string : "";
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    interface Listener {
        void onFragmentDetached(Fragment fragment);
        void onFragmentAttached(Fragment fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Listener) {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement fragments listener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
    }

    private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

            String user = getUserName(getActivity());
            String id = AccountUtils.getId(getActivity());

            Log.e(TAG, "user=="+user+"\nid=="+id);
            user += ":"+id;
            Log.e(TAG, "user=="+user);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpPost httppost = new HttpPost(
                        AppSettings.SERVER_URL+"update.php"); // server

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("image", id + ".png", in);

                httppost.setEntity(reqEntity);

                Log.e(TAG, "request " + httppost.getRequestLine());


                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                    Log.e(TAG, "response "+response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (response != null)
                        Log.e(TAG, "response " + response.getStatusLine().toString());
                } finally {

                }
            } finally {

            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
             super.onPostExecute(result);
            Toast.makeText(getActivity(), R.string.uploaded, Toast.LENGTH_LONG).show();
        }
    }
}
