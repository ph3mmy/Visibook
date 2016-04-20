package com.jcedar.visibook.lautech.gcm;

/**
 * Created by Afolayan on 12/10/2015.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.gcm.command.AddCommand;
import com.jcedar.visibook.lautech.gcm.command.UpdateCommand;
import com.jcedar.visibook.lautech.helper.AccountUtils;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.PrefUtils;
import com.jcedar.visibook.lautech.sync.SyncHelper;
import com.jcedar.visibook.lautech.ui.DashboardActivity;
import com.jcedar.visibook.lautech.ui.NewDashBoardActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GcmIntentServices extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GcmIntentService";
    private static final String PHONE_NUMBER = "phoneNumber";
    public static final String BUNDLE_ID_ARRAY = "array_of_sent_ids";
    NotificationCompat.Builder builder;
    public Uri uri;
    public int vibrate;
    public int defaultSound;
    Bundle syncSummary = Bundle.EMPTY;
    //Context context = getApplicationContext();
    public  final int birthdayId = 1111;
    public  final int multipleBirthdayId = 1112;
    public  final int newStudent = 2222;
    public  final int update = 3333;
    public static String[] ids;

    String phoneNumber;
    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;
    static {
        Map<String, GCMCommand> receivers = new HashMap<>();
        receivers.put("101", new UpdateCommand());
        receivers.put("201", new AddCommand());

        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }
    public GcmIntentServices() {
        super(AppSettings.GCM_SENDER_ID);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        /*
        From update: {"status":"Success","code":"201","id":"104"}
        From add: {"status":"Success","code":"101","id":"104"}
         */

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
              if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1) + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.d(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                Log.e(TAG, "Received: " + extras.toString());
                SyncHelper mSyncHelper = new SyncHelper(getApplicationContext());
                Log.d(TAG, "Sync Extras " + extras.toString());

                  String code = extras.getString("code");
                  int action = 0;
                  if (code != null) {
                      action = Integer.parseInt(code);
                  }
                  Log.e(TAG, action+" action");
                String message = extras.getString("Notice");
                //sendNotification(message);

                switch (action){

                    case 100: {
                        String response = extras.getString("response");

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int length = jsonArray.length();

                            if( length == 1){
                                //just one user's birthday
                                phoneNumber = jsonArray.getJSONObject(0).getString("phone");
                                AccountUtils.setPhoneNumber(this, phoneNumber);
                                Log.e(TAG, phoneNumber + " phoneNumber");

                                displayNotification(this, message, birthdayId, 0, "Birthday");
                            } else {
                                String[] allIds = new String[length];

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    allIds[i] = jsonArray.getJSONObject(i).getString("id");
                                }
                                ids = allIds;
                                Log.e(TAG, "ids "+ Arrays.toString(ids));

                                displayNotification(this, message, multipleBirthdayId, 0, "Birthday");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                        break;
                    case 101:
                        Log.e(TAG, "new name added");
                        syncSummary = mSyncHelper.performNewStudentSync();
                        int newStudentNo = syncSummary.getInt(SyncHelper.NEW_STUDENT_COUNT, 0);
                        if ( newStudentNo != 0 && PrefUtils.hasNotification(this) ) {
                            displayNotification(this, message, newStudent, newStudentNo, "New Student Added");
                        }
                        break;
                    case 201:
                        Log.e(TAG, "a name updated");
                        String updateId = extras.getString("updatedId");
                        long id = 0;
                        if (updateId != null) {
                            id = Long.parseLong(updateId);
                            syncSummary = mSyncHelper.performUpdateStudentSync(id);

                            int updateStudentNo = syncSummary.getInt( SyncHelper.UPDATE_COUNT, 0);
                            Log.e(TAG, "a name updated "+updateStudentNo);

                            if ( updateStudentNo != 0 && PrefUtils.hasNotification(this)) {
                                displayNotification(this, message, update, updateStudentNo,"Student Info Updated");
                            }
                        }

                        break;
                    case 202:
                        Log.e(TAG, "a name updated");
                        String updateId2 = extras.getString("updatedId");
                        long id2;
                        if (updateId2 != null) {
                            id2 = Long.parseLong(updateId2);

                            syncSummary = mSyncHelper.performUpdateStudentSync(id2);

                            int updateStudentNo2 = syncSummary.getInt( SyncHelper.UPDATE_COUNT, 0);
                            Log.e(TAG, "an image updated "+updateStudentNo2);

                            /**
                             * Don't notify in the case of an image update
                             */
                        }

                        break;
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DashboardActivity.class),
                0);

        Intent sendSMS = new Intent(Intent.ACTION_VIEW).putExtra("address", AccountUtils.getPhoneNumber(this)).setType("vnd.android-dir/mms-sms");
         PendingIntent smsIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), sendSMS, 0);

        Log.d(TAG, "phone number "+phoneNumber + " and "+AccountUtils.getPhoneNumber(this));
        Intent call = new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel: " + AccountUtils.getPhoneNumber(this)));
        PendingIntent callIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), call, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_user)
                        .setContentTitle("VisiBook")
                        .setSmallIcon(R.mipmap.ic_user)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .addAction(R.mipmap.ic_sms, "Send SMS", smsIntent )
                        .addAction(R.mipmap.ic_call, "Call", callIntent )
                        .setOngoing(true)
                        .setWhen(System.currentTimeMillis())
                ;

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void displayNotification(Context context, String message, int notifId, int number,
                                     String contentTitle){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NewDashBoardActivity.class),
                0);


        //Set sound
        if (PrefUtils.hasSound(context)) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            defaultSound = Notification.DEFAULT_SOUND;

        } else {
            uri = null;
        }
        // check vibration
        if (PrefUtils.hasVibration(context)) {
            vibrate = Notification.DEFAULT_VIBRATE;
        } else {
            vibrate = 0;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_user)
                        .setContentTitle(contentTitle)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message)
                        .setOngoing(true)
                        .setSound(uri)
                        .setAutoCancel(true)
                        .setDefaults( defaultSound | vibrate)
                        .setTicker("VisiBook Messages")
                        .setLights(Color.BLUE, 1000, 5000)

                        .setWhen(System.currentTimeMillis());


        switch ( notifId ){
             case birthdayId:
                Intent sendSMS = new Intent(Intent.ACTION_VIEW).putExtra("address", AccountUtils.getPhoneNumber(this))
                        .setType("vnd.android-dir/mms-sms");
                PendingIntent smsIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), sendSMS, 0);

                Log.d(TAG, "phone number "+phoneNumber + " and "+AccountUtils.getPhoneNumber(this));
                Intent call = new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel: " + AccountUtils.getPhoneNumber(this)));
                PendingIntent callIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), call, 0);

                 mBuilder.addAction(0, "Send SMS", smsIntent )
                     .addAction(0, "Call", callIntent );
                 /*mBuilder.addAction(R.mipmap.ic_sms, "Send SMS", smsIntent )
                     .addAction(R.mipmap.ic_call, "Call", callIntent );*/
                break;
            case multipleBirthdayId:
                Intent listS = new Intent(this, NewDashBoardActivity.class);
                Bundle b = new Bundle();

                b.putStringArray(BUNDLE_ID_ARRAY, ids);
                listS.putExtras(b);
                PendingIntent listIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), listS, 0);
                mBuilder.addAction(0, "View them", listIntent);
                break;
            case newStudent:
                mBuilder.setContentInfo( Integer.toString( number ));
                break;
            case update:
                mBuilder.setContentInfo( Integer.toString( number ));
                break;
        }

        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.PRIORITY_HIGH;
        mNotificationManager.notify(notifId, notification);

    }
}