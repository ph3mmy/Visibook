package com.jcedar.visibook.lautech.io.jsonhandlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.visibook.lautech.helper.Lists;
import com.jcedar.visibook.lautech.io.model.Student;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Afolayan on 13/10/2015.
 */
public class StudentUpdateHandler extends JSONHandler{
    private static final String TAG = StudentUpdateHandler.class.getSimpleName();
    private int studentCount;
    public StudentUpdateHandler(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(String json) throws IOException {
        if(json == null){
            return null;
        }
        Log.d(TAG, TextUtils.isEmpty(json) ? "Empty  Json" : json);

        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        Student[] currentStudent = Student.fromJson(json);
        studentCount = currentStudent.length;


        for ( Student student: currentStudent) {
            Log.e(TAG, "id is "+student.getId());
            try {
                Uri uri = DataContract.addCallerIsSyncAdapterParameter(
                        DataContract.Students.CONTENT_URI);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newUpdate(uri)
                        .withSelection(DataContract.Students._ID+"=?", new String[]{student.getId()})
                        .withValue(DataContract.Students.NAME, student.getName())
                        .withValue(DataContract.Students.GENDER, student.getGender())
                        .withValue(DataContract.Students.PHONE_NUMBER, student.getPhoneNumber())
                        .withValue(DataContract.Students.EMAIL, student.getEmail())
                        .withValue(DataContract.Students.CHAPTER, student.getChapter())
                        .withValue(DataContract.Students.COURSE, student.getCourse())
                        .withValue(DataContract.Students.DATE_OF_BIRTH, student.getDateOfBirth())
                        .withValue(DataContract.Students.DOB_NUMBER, student.getDobNumber())
                        .withValue(DataContract.Students.IS_ALUMNI, student.getIsAlumni())
                        .withValue(DataContract.Students.UPDATE_INFO, student.getUpdateInfo())
                        .withValue(DataContract.Students.UPDATED, String.valueOf( System.currentTimeMillis()));

                Log.d(TAG, "Data from Json" + student.getName() + " " + student.getChapter());

                batch.add(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batch;
    }

    public int getStudentCount() {
        return studentCount;
    }
}
