package com.jcedar.visibook.lautech.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Onyecar on 2/26/2015.
 */
public class DataContract
{
    //authority of data provider
    public static final String CONTENT_AUTHORITY = "com.jcedar.visibook.lautech.provider";

    //authority of base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Paths

    public static final String PATH_STUDENTS = "students";
    public static final String PATH_SEARCH = "search";
    public static final String PATH_STUDENTS_CHAPTER = "students_chapter";


    private static final String CALLER_IS_SYNCADAPTER = "caller_is_sync_adapter";
    public static final String PATH_SEARCH_INDEX = "search_index";



    public static class Students implements StudentColumns, BaseColumns, SyncColumns{
        /** Content URI for  students table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.visibook.lautech.provider.students";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.visibook.lautech.provider.students";

        public static Uri buildStudentUri(long studentId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(studentId)).build();
        }


        /** A projection of all tables in students table */
        public static final String[] PROJECTION_ALL = {
                _ID,  NAME, GENDER, CHAPTER, EMAIL, COURSE, PHONE_NUMBER, DATE_OF_BIRTH, DOB_NUMBER,
                UPDATED, IS_ALUMNI, UPDATE_INFO, IMAGE

    };

        /** The default sort order for queries containing students */
        public static final String SORT_ORDER_DEFAULT = CHAPTER +" ASC";

        public static Uri buildSearchUri(String query) {
            return CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).appendPath(query).build();
        }

        public static boolean isSearchUri(Uri uri) {
            List<String> pathSegments = uri.getPathSegments();
            return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
        }

        public static String getSearchQuery(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class StudentsChapter implements StudentChapterColumns, BaseColumns, SyncColumns{
        /** Content URI for  studentsChapter table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS_CHAPTER).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.visibook.lautech.provider.students_chapter";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.visibook.lautech.provider.students_chapter";

        public static Uri buildStudentUri(long studentId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(studentId)).build();
        }


        /** A projection of all tables in studentsChapter table */
        public static final String[] PROJECTION_ALL = {
                _ID,  ID, NAME, GENDER, CHAPTER, EMAIL, COURSE, PHONE_NUMBER, DATE_OF_BIRTH, DOB_NUMBER,
                UPDATED, IS_ALUMNI, UPDATE_INFO, IMAGE

        };

        /** The default sort order for queries containing students_chapter */
        public static final String SORT_ORDER_DEFAULT = NAME +" ASC";
    }



    public interface SyncColumns{
        String UPDATED = "updated";
    }

    interface StudentColumns{
        String NAME = "name";
        String GENDER = "gender";
        String CHAPTER = "chapter";
        String EMAIL = "email";
        String COURSE = "course";
        String PHONE_NUMBER = "phone_number";
        String DATE_OF_BIRTH = "date_of_birth";
        String DOB_NUMBER = "dob_number";
        String UPDATE_INFO= "updateInfo";
        String IS_ALUMNI = "isAlumni";
        String IMAGE = "image";
    }

    interface StudentChapterColumns{
        String ID = "ID";
        String NAME = "name";
        String GENDER = "gender";
        String CHAPTER = "chapter";
        String EMAIL = "email";
        String COURSE = "course";
        String PHONE_NUMBER = "phone_number";
        String DATE_OF_BIRTH = "date_of_birth";
        String DOB_NUMBER = "dob_number";
        String UPDATE_INFO= "updateInfo";
        String IS_ALUMNI = "isAlumni";
        String IMAGE = "image";
    }
    interface StudentSearchColumns {
        String SEARCH_STUDENT_ID = "student_id";
        String CONTENT = "content";
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                DataContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(DataContract.CALLER_IS_SYNCADAPTER));
    }
}
