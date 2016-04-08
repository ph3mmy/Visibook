package com.jcedar.visibook.lautech.ui;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.FormatUtils;
import com.jcedar.visibook.lautech.helper.ServiceHandler;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.provider.DataContract;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class UpdateFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {

    private static final String ARG_PARAM_ID = "_id";
    private static final String TAG = UpdateFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private long student_id;
    private TextView detail;
    EditText name, email, course, phone_number, date;
    RadioButton male, female;
    SwitchCompat alumniSwitch;

    String nameStr, emailStr, courseStr, phone_numberStr, gender, isAlumni;
    private Calendar newCalendar = Calendar.getInstance();
    private Calendar currentDate;
    private String dobNumber, dobString;
    private String dateStr;
    private String oldDate;
    private String id;


    public UpdateFragment() {
        // Required empty public constructor
    }


    public static UpdateFragment newInstance(long id) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM_ID, id);
        Log.e(TAG, "id in here "+id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            student_id = getArguments().getLong(ARG_PARAM_ID);
        }

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_update, container, false);
        //detail = (TextView) rootView.findViewById( R.id.);
        name = (EditText) rootView.findViewById( R.id.etName );
        email = (EditText) rootView.findViewById( R.id.etEmail );
        course = (EditText) rootView.findViewById( R.id.etCourse );
        phone_number = (EditText) rootView.findViewById( R.id.etPhone );
        date = (EditText) rootView.findViewById( R.id.etDOB );

        male = (RadioButton) rootView.findViewById( R.id.rbMale );
        female = (RadioButton) rootView.findViewById( R.id.rbFemale );

        alumniSwitch = (SwitchCompat) rootView.findViewById( R.id.switch1 );
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameStr = name.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                courseStr = course.getText().toString().trim();
                phone_numberStr = phone_number.getText().toString().trim();
                dateStr = date.getText().toString().trim();

                gender = ( male.isChecked() ? "Male": "Female");

                isAlumni = ( alumniSwitch.isChecked() ? "1": "0");

                UIUtils.showToast( getActivity(), "Updating name");
                /*updateThread.start();
                try {
                    updateThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                new PostUpdate().execute();
                getActivity().getFragmentManager().popBackStack();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = DatePickerDialog.newInstance(UpdateFragment.this,
                        newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH)
                );
                //initialize the current date
                currentDate = Calendar.getInstance();

                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);

                        dobNumber = FormatUtils.makeYearMonthDay(newDate.getTime());
                        dobString = FormatUtils.makeTrimmedYear(newDate.getTime());
                        date.setText( dobString );

                    }
                });

                dialog.setAccentColor(R.color.theme_primary);
                dialog.dismissOnPause(true);
                dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });
        getDialog().setTitle("Update names");

        return rootView;
    }

    Thread updateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            updateExistingEntries(id, nameStr,
                    gender, emailStr, courseStr, phone_numberStr,
                    dateStr, dobNumber, isAlumni);
        }
    });

    private class PostUpdate extends AsyncTask<Void, Void, Void>{
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Updating the database...");
            dialog.setTitle("Please Wait");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateExistingEntries(id, nameStr,
                    gender, emailStr, courseStr, FormatUtils.removeEscapeXters(phone_numberStr),
                    dateStr, dobNumber, isAlumni);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if ( dialog.isShowing() ) dialog.dismiss();
            UIUtils.showToast(getActivity(), "Please wait till you receive a notification");
            /*if( getTag().equalsIgnoreCase("User")){ // from user's profile
                startActivity( new Intent(getActivity(), NewDashBoardActivity.class));
                getDialog().dismiss();
            }*/
            getDialog().dismiss();
            //startActivity( new Intent(getActivity(), NewDashBoardActivity.class));
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(1);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = DataContract.Students.buildStudentUri(student_id);
        return new CursorLoader( getActivity(),
                uri,
                DataContract.Students.PROJECTION_ALL,
                null, null,
                DataContract.Students.SORT_ORDER_DEFAULT);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        id = data.getString( data.getColumnIndex(DataContract.Students._ID));
        name.setText(data.getString( data.getColumnIndex(DataContract.Students.NAME)));

        email.setText(data.getString( data.getColumnIndex(DataContract.Students.EMAIL)));

        course.setText(data.getString( data.getColumnIndex(DataContract.Students.COURSE)));

        phone_number.setText(data.getString( data.getColumnIndex(DataContract.Students.PHONE_NUMBER)));

        oldDate = data.getString( data.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
        date.setText(oldDate);

        String gender = data.getString( data.getColumnIndex(DataContract.Students.GENDER));
        int alumni = data.getInt( data.getColumnIndex(DataContract.Students.IS_ALUMNI));

        boolean isAlumni = ( alumni != 0 );
        Log.e(TAG, alumni+ " is alumni "+isAlumni);
        if (isAlumni) alumniSwitch.setChecked( true );

        if ( gender.startsWith("M")) male.setChecked(true);
        else female.setChecked(true);
        Log.e(TAG, gender+ " gender ");


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }



    public void updateExistingEntries(String id, String name, String gender, String email,
                                             String course, String phone_number, String dateStr,
                                             String dobNumber, String isAlumni){
        String userId = AccountUtils.getId( getActivity() );
        String userChapter = AccountUtils.getUserChapter( getActivity() );
        Log.e(TAG, userId+" "+userChapter+" \n"+id+" "+name +" \n"+gender+ " "+email+" \n"
                +course+" "+phone_number+" \n"+dateStr+" "+dobNumber+
                " \n"+ isAlumni
        );
        if ( userId != null && userChapter != null ){
        //if ( userChapter != null ){
            Log.e(TAG, userId+"updating student "+name +"'s details ");
            String url = String.format(AppSettings.SERVER_URL
                            +"update.php?" +
                            "id=%s" +
                            "&name=%s" +
                            "&gender=%s" +
                            "&school=%s" +
                            "&course=%s" +
                            "&email=%s" +
                            "&phone_number=%s" +
                            "&dobNumber=%s" +
                            "&dobString=%s"+
                            "&userId=%s"+
                            "&isAlumni=%s",

                    id, name, gender, userChapter,
                    course, email, phone_number,
                    dobNumber, dateStr, userId, isAlumni

            );


            String response =  ServiceHandler.makeServiceCall (url, ServiceHandler.GET);
            if(response == null){
                return;
            }
        }

    }
}