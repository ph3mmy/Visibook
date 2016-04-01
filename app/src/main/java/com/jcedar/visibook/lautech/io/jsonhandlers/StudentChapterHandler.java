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
public class StudentChapterHandler extends JSONHandler{
    private static final String TAG = StudentChapterHandler.class.getSimpleName();

    public StudentChapterHandler(Context context) {
        super(context);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(String json) throws IOException {
        if(json == null){
            return null;
        }
        Log.d(TAG, TextUtils.isEmpty(json) ? "Empty Student Json" : json);

        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        Student[] currentStudent = Student.fromJson(json);


        for ( Student student: currentStudent) {
            try {
                Uri uri = DataContract.addCallerIsSyncAdapterParameter(
                        DataContract.StudentsChapter.CONTENT_URI);
                ContentProviderOperation.Builder builder = ContentProviderOperation
                        .newInsert(uri)
                        .withValue(DataContract.StudentsChapter.ID, student.getId())
                        .withValue(DataContract.StudentsChapter.NAME, student.getName())
                        .withValue(DataContract.StudentsChapter.GENDER, student.getGender())
                        .withValue(DataContract.StudentsChapter.EMAIL, student.getEmail())
                        .withValue(DataContract.StudentsChapter.CHAPTER, student.getChapter())
                        .withValue(DataContract.StudentsChapter.COURSE, student.getCourse())
                        .withValue(DataContract.StudentsChapter.DATE_OF_BIRTH, student.getDateOfBirth())
                        .withValue(DataContract.StudentsChapter.DOB_NUMBER, student.getDobNumber())
                        .withValue(DataContract.StudentsChapter.PHONE_NUMBER, student.getPhoneNumber())
                        .withValue(DataContract.StudentsChapter.IS_ALUMNI, student.getIsAlumni())
                        .withValue(DataContract.StudentsChapter.UPDATE_INFO, student.getUpdateInfo())
                        .withValue(DataContract.StudentsChapter.UPDATED, String.valueOf(System.currentTimeMillis()));

                Log.d(TAG, "Data from Json" + student.getName() + " " + student.getChapter());

                batch.add(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return batch;
    }
}
