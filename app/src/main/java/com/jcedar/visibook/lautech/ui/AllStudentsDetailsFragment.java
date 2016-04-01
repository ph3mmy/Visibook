package com.jcedar.visibook.lautech.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.provider.DataContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllStudentsDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    public static final String ARGS_ALL_STUDENT_ID = "all_student_id";
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = AllStudentsDetailsFragment.class.getSimpleName();
    private Handler handler;
    private static Uri dataUri;
    private TextView name, description, gender, chapter, email, course, phoneNumber, dateOfBirth;
    private String mParam1 = "mParam1";
    private String cId ;
    Toolbar toolbar, activityToolbar;
    private String nameStr = "";
    private String emailAdd = "";
    private String phone = "";
    private ImageView imgSendEmail;
    private ImageView imgSendSms;
    private ImageView imgCall;


    public AllStudentsDetailsFragment() {
    }

    public static Fragment newInstance(int position,
                                       AllStudentDetailsActivity studentDetailsActivity) {
        AllStudentsDetailsFragment fragment = new AllStudentsDetailsFragment();
        Bundle args = new Bundle();
        long _id = studentDetailsActivity.mStudents.get(position);
        args.putLong(ARGS_ALL_STUDENT_ID, _id);
        fragment.setArguments(args);
        return fragment;
    }
    public static AllStudentsDetailsFragment newInstance(Uri uri,
                                       AllStudentDetailsActivity studentDetailsActivity) {
        AllStudentsDetailsFragment fragment = new AllStudentsDetailsFragment();
        Log.e(TAG, uri+" uri");
        dataUri = uri;
        return fragment;
    }

    public static AllStudentsDetailsFragment newInstance(long studentId) {
        AllStudentsDetailsFragment fragment = new AllStudentsDetailsFragment();

        dataUri = DataContract.Students.buildStudentUri(studentId );
        Log.e(TAG, dataUri+" uri inside search");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        Bundle args = getArguments();
        if (args != null) {
            mParam1 = args.getString(ARG_PARAM1);
            String payItemId = Long.toString(args.getLong(ARGS_ALL_STUDENT_ID));
            cId = payItemId;

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, Bundle.EMPTY, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_person_details, container, false);

         toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }
        String context = getActivity().getClass().getSimpleName();
        if ( context.equals("AllStudentDetailsActivity"))
            activityToolbar = ((AllStudentDetailsActivity) getActivity()).getActionBarToolbar();
        else {
            activityToolbar = ((SearchActivity) getActivity()).getActionBarToolbar();
        }


        imgSendEmail = (ImageView) rootView.findViewById(R.id.send_email);
        imgSendSms = (ImageView) rootView.findViewById(R.id.send_message);
        imgCall = (ImageView) rootView.findViewById(R.id.call_phone);

        imgCall.setOnClickListener(this);
        imgSendSms.setOnClickListener(this);
        imgSendEmail.setOnClickListener(this);

        name = (TextView) rootView.findViewById(R.id.tvName);
        gender = (TextView) rootView.findViewById(R.id.tvGender);
        chapter = (TextView) rootView.findViewById(R.id.tvChapter);
        email = (TextView) rootView.findViewById(R.id.tvEmail);
        course = (TextView) rootView.findViewById(R.id.tvCourse);
        phoneNumber =  (TextView) rootView.findViewById(R.id.tvPhoneNumber);
        dateOfBirth = (TextView) rootView.findViewById(R.id.tvDateOfBirth);
        description = (TextView) rootView.findViewById(R.id.tvDescription);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), dataUri,
                DataContract.Students.PROJECTION_ALL, null, null, null);

        /*return new CursorLoader(getActivity(),
                DataContract.Students.CONTENT_URI,
                DataContract.Students.PROJECTION_ALL,
                DataContract.Students._ID +"=?",
                new String[]{ cId}, null);*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getActivity() == null) {
            return;
        }

        if(data != null && data.moveToFirst()) {
            nameStr = data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.NAME));
            name.setText(nameStr);

            Log.e(TAG, nameStr + " name");

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if( toolbar != null) {
                        toolbar.setTitle(nameStr);
                    } else{
                        if ( activityToolbar != null){
                            activityToolbar.setTitle(nameStr);
                        }
                    }
                }
            });

            String genderStr1 = data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.GENDER));
            if(genderStr1.startsWith("M"))
                gender.setText("Male");

            else gender.setText("Female");

            chapter.setText(data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.CHAPTER)));


            emailAdd = data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.EMAIL));
            email.setText(emailAdd);

            course.setText(data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.COURSE)));


            phone = data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.PHONE_NUMBER));
            phoneNumber.setText(phone);

            dateOfBirth.setText(data.getString(
                    data.getColumnIndexOrThrow(DataContract.Students.DATE_OF_BIRTH)));

            data.close();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.call_phone:
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Call")
                        .setMessage("Do you want to make a call to "+nameStr +"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (phone != null) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + phone));
                                    startActivity(callIntent);
                                } else {
                                    UIUtils.showToast(getActivity(), "No number to call");
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
            case R.id.send_message:

                AlertDialog dialog1 = new AlertDialog.Builder(getActivity())
                        .setTitle("Message")
                        .setMessage("Do you want to send a message to "+nameStr +"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (phone != null) {
                                    Uri smsUri = Uri.parse("tel:" + phone);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                                    intent.putExtra("address", phone);
                                    intent.putExtra("sms_body", "");
                                    intent.setType("vnd.android-dir/mms-sms");
                                    startActivity(intent);
                                } else {
                                    UIUtils.showToast(getActivity(), "No message recipient");
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.send_email:
                if (emailAdd != null) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", emailAdd, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                } else {
                    Toast.makeText(getActivity(), "User has no email", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(0, null, AllStudentsDetailsFragment.this);
        }
    };
}
