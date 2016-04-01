package com.jcedar.visibook.lautech.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.provider.DataContract;

public class Details extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    private ImageView imgSendEmail, imgSendSms, imgCall;
    private TextView name, gender, chapter, email, course, phoneNumber, dateOfBirth, description;
    private String nameStr, emailAdd, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        imgSendEmail = (ImageView) findViewById(R.id.send_email);
        imgSendSms = (ImageView) findViewById(R.id.send_message);
        imgCall = (ImageView) findViewById(R.id.call_phone);

        imgCall.setOnClickListener(this);
        imgSendSms.setOnClickListener(this);
        imgSendEmail.setOnClickListener(this);

        name = (TextView) findViewById(R.id.tvName);
        gender = (TextView) findViewById(R.id.tvGender);
        chapter = (TextView) findViewById(R.id.tvChapter);
        email = (TextView) findViewById(R.id.tvEmail);
        course = (TextView) findViewById(R.id.tvCourse);
        phoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        dateOfBirth = (TextView) findViewById(R.id.tvDateOfBirth);
        description = (TextView) findViewById(R.id.tvDescription);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            nameStr = bundle.getString(DataContract.StudentsChapter.NAME);
            name.setText(nameStr);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    toolbar.setTitle(nameStr);
                }
            });

            String genderStr1 = bundle.getString(DataContract.StudentsChapter.GENDER);
            if (genderStr1.equalsIgnoreCase("M"))
                gender.setText("Male");

            else if (genderStr1.equalsIgnoreCase("F"))
                gender.setText("Female");

            chapter.setText(bundle.getString(DataContract.StudentsChapter.CHAPTER));


            emailAdd = bundle.getString(DataContract.StudentsChapter.EMAIL);
            email.setText(emailAdd);

            course.setText(bundle.getString(DataContract.StudentsChapter.COURSE));


            phone = bundle.getString(DataContract.StudentsChapter.PHONE_NUMBER);
            phoneNumber.setText(phone);

            dateOfBirth.setText(bundle.getString(DataContract.StudentsChapter.DATE_OF_BIRTH));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.call_phone:
                Log.e("TAG", "Name " + nameStr);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Call")
                        .setMessage("Do you want to make a call to " + nameStr + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (phone != null) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + phone));
                                    if (ActivityCompat.checkSelfPermission(Details.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(callIntent);
                                } else {
                                    UIUtils.showToast(Details.this, "No number to call");
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

                AlertDialog dialog1 = new AlertDialog.Builder(this)
                        .setTitle("Call")
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
                                    UIUtils.showToast(Details.this, "No message recipient");
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
                    Toast.makeText(this, "User has no email", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
