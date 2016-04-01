package com.jcedar.visibook.lautech.ui;

import android.app.Activity;
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
public class StudentDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    private static final String ARGS_STUDENT_ID = "student_id";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = StudentDetailsFragment.class.getSimpleName();
    private Handler handler;
    private TextView name, description, gender, chapter, email, course, phoneNumber, dateOfBirth;
    private String mParam1 = "param1";
    private String cId;
    private String mParam2 = "param2";
    private Toolbar toolbar, bottom_toolbar;
    private String phone;
    private String emailAdd;

    private DetailsListener listener;

    public static String nameStr="";
    private ImageView imgSendEmail;
    private ImageView imgSendSms;
    private ImageView imgCall;
    static Uri dataUri;
    private Toolbar activityToolbar;

    public StudentDetailsFragment() {
    }

    public static Fragment newInstance(int position,
                                       StudentDetailsActivity studentDetailsActivity) {
        StudentDetailsFragment fragment = new StudentDetailsFragment();
        Bundle args = new Bundle();
        long _id = studentDetailsActivity.mStudents.get(position);
        args.putLong(ARGS_STUDENT_ID, _id);
        fragment.setArguments(args);
        return fragment;
    }

    public static StudentDetailsFragment newInstance(Uri uri,
                                                         StudentDetailsActivity studentDetailsActivity) {
        StudentDetailsFragment fragment = new StudentDetailsFragment();
        Log.e(TAG, uri+" uri");
        dataUri = uri;
        Log.e(TAG, dataUri+" data uri");
        return fragment;
    }

    public static Fragment newInstance(String studentId, String something) {
        StudentDetailsFragment fragment = new StudentDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, Long.parseLong(studentId));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        handler = new Handler();
        Bundle args = getArguments();
        if (args != null) {
            mParam1 = args.getString(ARG_PARAM1);
            String itemId = Long.toString(args.getLong(ARGS_STUDENT_ID));
            cId = itemId;
            mParam2 = args.getString(ARG_PARAM2);

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
        activityToolbar = ((StudentDetailsActivity) getActivity()).getActionBarToolbar();

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

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof DetailsListener) {
            listener = (DetailsListener) activity;
           listener.onFragmentAttached(this);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement DetailsListener");
        }
        activity.getContentResolver().registerContentObserver(
                DataContract.StudentsChapter.CONTENT_URI, true, mObserver);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof DetailsListener) {
            ((DetailsListener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), dataUri,
                DataContract.StudentsChapter.PROJECTION_ALL, null, null, null);
        /*return new CursorLoader(getActivity(),
                DataContract.StudentsChapter.CONTENT_URI,
                DataContract.StudentsChapter.PROJECTION_ALL,
                DataContract.StudentsChapter._ID +"=?",
                new String[]{ cId}, null);*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getActivity() == null) {
            return;
        }

        if(data != null && data.moveToFirst()) {
             nameStr = data.getString(
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.NAME));
            name.setText(nameStr);

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
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.GENDER));

            if(genderStr1.startsWith("M"))
                gender.setText("Male");

            else
                gender.setText("Female");

           chapter.setText(data.getString(
                   data.getColumnIndexOrThrow(DataContract.StudentsChapter.CHAPTER)));


            emailAdd = data.getString(
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.EMAIL));
            email.setText(emailAdd);

            course.setText(data.getString(
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.COURSE)));


             phone = data.getString(
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.PHONE_NUMBER));
            phoneNumber.setText(phone);

            dateOfBirth.setText(data.getString(
                    data.getColumnIndexOrThrow(DataContract.StudentsChapter.DATE_OF_BIRTH)));

            data.close();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(0, null, StudentDetailsFragment.this);
        }
    };

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.call_phone:
                Log.e(TAG, "Name "+nameStr);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Call")
                        .setMessage("Do you want to make a call to "+ phone +"?")
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
                        .setTitle("Call")
                        .setMessage("Do you want to send a message to "+phone +"?")
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
    public interface DetailsListener {
        void getUserData(String phoneNumber, String emailAddress, String name);
        void onFragmentAttached(Fragment fragment);
        void onFragmentDetached(Fragment fragment);
    }
      /*
        }*/



}
