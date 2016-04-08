package com.jcedar.visibook.lautech.helper;

/*
import com.jcedar.visibook.lautech.provider.DataContract;
import com.jcedar.visibook.lautech.util.PrefUtils;
*/

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.jcedar.visibook.lautech.io.jsonhandlers.StudentChapterHandler;
import com.jcedar.visibook.lautech.io.jsonhandlers.StudentHandler;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.io.IOException;
import java.util.ArrayList;

public class AppHelper  {

    private static final String TAG = AppHelper.class.getSimpleName();

    public static Context context;

    public AppHelper( ) {

    }

    public static void pullAndSaveAllStudentData(Context context){

        try {
             String response =  ServiceHandler.makeServiceCall
                           (AppSettings.SERVER_URL +"get_all_students.php", ServiceHandler.GET);
             if(response == null){
                  return;
                }
            Log.e(TAG, " response" + response);

             ArrayList<ContentProviderOperation> operations =
                           new StudentHandler(context).parse(response);
               if (operations.size() > 0) {
                   ContentResolver resolver = context.getContentResolver();
                   resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);
               }
            }catch (IOException | OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }


    }

    public static void pullAndSaveStudentChapterData(Context context){
        try {
                    String chapter="";
                    if ( AccountUtils.getUserChapter(context) != null){
                        chapter = AccountUtils.getUserChapter(context);
                    }
                    Log.e(TAG, "starting student chapter data response");
                    String response =  ServiceHandler.makeServiceCall
                            (AppSettings.SERVER_URL +"getUsersChapter.php?chapter="+chapter, ServiceHandler.GET);
                    if(response == null){
                        return;
                    }
                    Log.e(TAG, response + " response");

                    ArrayList<ContentProviderOperation> operations =
                            new StudentChapterHandler(context).parse(response);
                    if (operations.size() > 0) {
                        ContentResolver resolver = context.getContentResolver();
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                    }


                }catch (IOException | OperationApplicationException | RemoteException e) {
                    e.printStackTrace();
                }



    }
    public void pullAndSaveStudentChapterDataForSync(Context context){

                try {

                    Log.e(TAG, "starting student chapter data response");
                    String response =  ServiceHandler.makeServiceCall
                            (AppSettings.SERVER_URL +"get_all_students.php", ServiceHandler.GET);
                            //(AppSettings.SERVER_URL +"get_user_chapter.php?chapter="+chapter, ServiceHandler.GET);
                    if(response == null){
                        return;
                    }
                    Log.e(TAG, response + " response update");

                    /*DataProvider dp = new DataProvider();
                    dp.deleteRecreateTable(); //Recreate the student_chapter table
*/
                    ArrayList<ContentProviderOperation> operations =
                            new StudentChapterHandler(context).parse(response);
                    if (operations.size() > 0) {
                        ContentResolver resolver = context.getContentResolver();
                        resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                    }


                }catch (IOException | OperationApplicationException | RemoteException e) {
                    e.printStackTrace();
                }

    }



}