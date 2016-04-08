package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.ui.view.Fab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seyi.Afolayan on 4/7/2016.
 */
public class ProfileFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_CODE = 1;
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView fullName, emailView, dobView, courseView, numberView;
    private Context context;
    private Listener mCallback;

    private Fab fab1;
    private MaterialSheetFab<Fab> materialSheetFab;

    LinearLayout changeImage;
    LinearLayout changeProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_user_profile, container, false);

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
        Drawable d = new BitmapDrawable(getResources(), PrefUtils.decodeBase64(PrefUtils.getPhoto(context)));

        toolbarLayout.setStatusBarScrim(d);
        toolbarLayout.setStatusBarScrimColor(UIUtils.getDominantColor(PrefUtils.decodeBase64(PrefUtils.getPhoto(context))));

        imageView.setImageBitmap(UIUtils.getProfilePic(context));
        fullName.setText(AccountUtils.getUserName(context));
        String email = AccountUtils.getUserEmail(context);
        String dob = AccountUtils.getUserDOB(context);
        String course = AccountUtils.getUserCourse(context);
        String phoneNumber = AccountUtils.getUserPhoneNumber(context);


        emailView.setText( isNotNull(email));
        dobView.setText( isNotNull(dob));



        courseView.setText( isNotNull(course) );
        numberView.setText(isNotNull(phoneNumber));



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

        return view;
    }



    private Uri outputFileUri;

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname="img_"+AccountUtils.getFullName(getActivity()).replace(" ", "_")+".jpg";// = Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
            }
        //}
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
}
