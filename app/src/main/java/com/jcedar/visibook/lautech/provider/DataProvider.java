package com.jcedar.visibook.lautech.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jcedar.visibook.lautech.helper.SelectionBuilder;

import java.util.Arrays;

public class DataProvider extends ContentProvider {

    private static final String TAG = DataProvider.class.getName();
    DatabaseHelper mOpenHelper = null;
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static final int STUDENT_ID = 101;
    private static final int STUDENT_LIST = 102;
    private static final int STUDENT_SEARCH = 103;

    private static final int STUDENT_CHAPTER_ID = 201;
    private static final int STUDENT_CHAPTER_LIST = 202;

    private static final int SEARCH_INDEX = 1701;


    public DataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Log.v(TAG, "delete(uri=" + uri + ", values=" + Arrays.toString(selectionArgs) + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // TODO: Handle signOut
        if (uri == DataContract.BASE_CONTENT_URI) {
            // Handle whole database deletes (e.g. when signing out)
            deleteDatabase();
            notifyChange(uri, false);
            return 1;
        }


        int match = sUriMatcher.match(uri);
        if (match == STUDENT_LIST || match == STUDENT_ID) {
            DatabaseHelper.rebuildDashbaord(db);
            return 1;
        }

        final SelectionBuilder builder = buildSelection(uri, match);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri, !DataContract.hasCallerIsSyncAdapterParameter(uri));
        return retVal;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case STUDENT_LIST:
                 return DataContract.Students.CONTENT_TYPE;
            case STUDENT_ID:
                return DataContract.Students.CONTENT_ITEM_TYPE;

            case STUDENT_CHAPTER_LIST:
                return DataContract.StudentsChapter.CONTENT_TYPE;
            case STUDENT_CHAPTER_ID:
                return DataContract.StudentsChapter.CONTENT_ITEM_TYPE;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + ")");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long id;
        boolean syncToNetwork = DataContract.hasCallerIsSyncAdapterParameter(uri);
        switch (match) {
            case STUDENT_LIST: {
                id = db.insertOrThrow(DatabaseHelper.Tables.STUDENTS, null, values);
                notifyChange(uri, syncToNetwork);
                return getUriForId(id, uri);

            }case STUDENT_CHAPTER_LIST: {
                id = db.insertOrThrow(DatabaseHelper.Tables.STUDENTS_CHAPTER, null, values);
                notifyChange(uri, syncToNetwork);
                return getUriForId(id, uri);
            }



            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        // The bulk of the setting is done inside DataProvider.buildSelection()
        final SelectionBuilder builder = buildSelection(uri, match);
        switch (match) {
            case STUDENT_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Students.SORT_ORDER_DEFAULT;
                }
                break;
            case STUDENT_CHAPTER_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.StudentsChapter.SORT_ORDER_DEFAULT;
                }
                break;


            default:
                break;
        }
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "uri and match in update : " + uri + "match" + match);
        if (match == SEARCH_INDEX) {
            // update the search index
            Log.d(TAG, "calling updateSearchIndex ");
            DatabaseHelper.updateSearchIndex(db);
            return 1;
        }

        final SelectionBuilder builder = buildSelection(uri, match);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        boolean syncToNetwork = !DataContract.hasCallerIsSyncAdapterParameter(uri);
        notifyChange(uri, syncToNetwork);
        return retVal;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, DataContract.PATH_STUDENTS, STUDENT_LIST);
        matcher.addURI(authority, DataContract.PATH_STUDENTS + "/#", STUDENT_ID);

        matcher.addURI(authority, "students/search/*", STUDENT_SEARCH);
        matcher.addURI(authority, DataContract.PATH_SEARCH_INDEX, SEARCH_INDEX);

        matcher.addURI(authority, DataContract.PATH_STUDENTS_CHAPTER, STUDENT_CHAPTER_LIST);
        matcher.addURI(authority, DataContract.PATH_STUDENTS_CHAPTER + "/#", STUDENT_CHAPTER_ID);

        return matcher;
    }

    private static Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            return itemUri;
        }
        //something went wrong
        throw new SQLException("Problem inserting into uri: " + uri);
    }

    private void notifyChange(Uri uri, boolean syncToNetwork) {
        Context context = getContext();
        if( context != null) {
            ContentResolver resolver = context.getContentResolver();
            resolver.notifyChange(uri, null, syncToNetwork);
        }

        // Widgets can't register content observers so we refresh widgets separately.
        // context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }

    private SelectionBuilder buildSelection(Uri uri, int match) {

        Log.d(TAG, "uri and match:" + uri + "match" + match);
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case STUDENT_LIST: {
                return builder.table(DatabaseHelper.Tables.STUDENTS);
            }
            case STUDENT_ID: {
                final String id = uri.getLastPathSegment();
                return builder.table(DatabaseHelper.Tables.STUDENTS)
                        .where(DataContract.Students._ID + "=?", id);
            }
            case STUDENT_SEARCH: {
                final String query = DataContract.Students.getSearchQuery(uri);
                return builder.table(DatabaseHelper.Tables.STUDENT_SEARCH_JOIN)
                        .mapToTable(DataContract.Students.NAME, DatabaseHelper.Tables.STUDENTS)
                        .mapToTable(DataContract.Students._ID, DatabaseHelper.Tables.STUDENTS)
                        .mapToTable(DataContract.StudentSearchColumns.CONTENT, DatabaseHelper.Tables.STUDENT_SEARCH)
                        .where(DataContract.StudentSearchColumns.CONTENT + " MATCH ?", query);
            }

            case STUDENT_CHAPTER_LIST: {
                return builder.table(DatabaseHelper.Tables.STUDENTS_CHAPTER);
            }
            case STUDENT_CHAPTER_ID: {
                final String id = uri.getLastPathSegment();
                return builder.table(DatabaseHelper.Tables.STUDENTS_CHAPTER)
                        .where(DataContract.StudentsChapter._ID + "=?", id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }


    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mOpenHelper.close();
        Context context = getContext();
        DatabaseHelper.deleteDatabase(context);
        mOpenHelper = new DatabaseHelper(getContext());
    }

}
