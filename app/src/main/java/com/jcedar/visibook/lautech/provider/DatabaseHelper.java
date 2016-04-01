package com.jcedar.visibook.lautech.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "visibook.db";
    private static final int DATABASE_VERSION = 101;
    private static String TAG = DatabaseHelper.class.getName();
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_STUDENT_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_CHAPTER_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_SEARCH_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENT_SEARCH);
        onCreate(db);
    }

//    public static void updateMonthlySummary(SQLiteDatabase db) {
//        db.execSQL("DELETE FROM " + Tables.MONTLY_SUMMARY);
//        //db.execSQL(SQL_UPDATE_SEARCH_TABLE);
//
//    }

    final static String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE "
            + Tables.STUDENTS + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DataContract.Students.NAME + " VARCHAR NOT NULL,"
            + DataContract.Students.GENDER + " VARCHAR NOT NULL,"
            + DataContract.Students.CHAPTER + " VARCHAR NOT NULL,"
            + DataContract.Students.EMAIL + " VARCHAR NOT NULL,"
            + DataContract.Students.COURSE + " VARCHAR NOT NULL,"
            + DataContract.Students.PHONE_NUMBER + " VARCHAR ,"
            + DataContract.Students.DATE_OF_BIRTH + " VARCHAR ,"
            + DataContract.Students.DOB_NUMBER + " VARCHAR ,"
            + DataContract.Students.IS_ALUMNI + " VARCHAR ,"
            + DataContract.Students.UPDATE_INFO + " VARCHAR ,"
            + DataContract.Students.UPDATED + " LONG DEFAULT 0, "
            + "UNIQUE (" + DataContract.Students.NAME + ","
            + DataContract.Students.EMAIL
            + ") ON CONFLICT REPLACE )" ;



    public final static String SQL_CREATE_STUDENTS_CHAPTER_TABLE = "CREATE TABLE "
            + Tables.STUDENTS_CHAPTER + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataContract.StudentsChapter.ID + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.NAME + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.GENDER + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.CHAPTER + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.EMAIL + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.COURSE + " VARCHAR NOT NULL, "
            + DataContract.StudentsChapter.PHONE_NUMBER + " VARCHAR, "
            + DataContract.StudentsChapter.DATE_OF_BIRTH + " VARCHAR, "
            + DataContract.StudentsChapter.IS_ALUMNI + " VARCHAR , "
            + DataContract.StudentsChapter.UPDATE_INFO + " VARCHAR,"
            + DataContract.StudentsChapter.DOB_NUMBER + " VARCHAR, "
            + DataContract.StudentsChapter.UPDATED + " LONG DEFAULT 0, "
            + "UNIQUE (" + DataContract.StudentsChapter.NAME + ","
            + DataContract.StudentsChapter.EMAIL
            + ") ON CONFLICT REPLACE )" ;

    final static String SQL_CREATE_STUDENTS_SEARCH_TABLE = "CREATE VIRTUAL TABLE "
            + Tables.STUDENT_SEARCH + " USING fts3("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataContract.StudentSearchColumns.CONTENT + " TEXT NOT NULL, "
            + DataContract.StudentSearchColumns.SEARCH_STUDENT_ID + " VARCHAR NOT NULL,"
            + "tokenize=simple)";

    final static String SQL_UPDATE_SEARCH_TABLE = "INSERT INTO " + Tables.STUDENT_SEARCH
            + "(" + DataContract.StudentSearchColumns.SEARCH_STUDENT_ID + ","
            + DataContract.StudentSearchColumns.CONTENT + ")"

            + " SELECT " + DataContract.Students._ID + ", ("
            + DataContract.Students.NAME + "||'; '||"
            + DataContract.Students.CHAPTER + "||'; '||"
            + DataContract.Students.COURSE + "||'; '||"
            + DataContract.Students.EMAIL + "||'; '||"
            + DataContract.Students.PHONE_NUMBER + ")"

            + " FROM " + Tables.STUDENTS;
    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


    private void upgradeDb(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public static void updateSearchIndex(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + Tables.STUDENT_SEARCH);
        db.execSQL(SQL_UPDATE_SEARCH_TABLE);
        Log.d(TAG, "Search table updating");
    }

    interface Tables {
        String STUDENTS = "students";
        String STUDENT_SEARCH = "student_search";
        String STUDENTS_CHAPTER = "students_chapter";

        String STUDENT_SEARCH_JOIN = Tables.STUDENTS
                + " INNER JOIN "+ Tables.STUDENT_SEARCH+" ON "
                + Tables.STUDENTS+"."+DataContract.Students._ID +"="
                + Tables.STUDENT_SEARCH+"."+DataContract.StudentSearchColumns.SEARCH_STUDENT_ID;


    }

    public static void rebuildDashbaord(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENTS_CHAPTER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENT_SEARCH);

        /*db.execSQL(SQL_CREATE_STUDENT_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_CHAPTER_TABLE);
        db.execSQL(SQL_CREATE_STUDENTS_SEARCH_TABLE);*/

    }

    public Cursor getSearch(String query){
        Cursor c = mContext.getContentResolver().query(
                DataContract.Students.CONTENT_URI,
                DataContract.Students.PROJECTION_ALL,
                DataContract.Students.NAME +" LIKE ? ",
                new String[]{query},
                DataContract.Students._ID +" ASC"
                );
        /*
        * Cursor c = mContext.getContentResolver().query(
                DataContract.Students.CONTENT_URI,
                DataContract.Students.PROJECTION_ALL,
                DataContract.Students.NAME +" LIKE ? OR "
                        +DataContract.Students.PHONE_NUMBER +" LIKE ? OR "
                        +DataContract.Students.EMAIL +" LIKE ? OR "
                        +DataContract.Students.GENDER +" LIKE ? OR "
                        +DataContract.Students.COURSE +" LIKE ? OR "
                        +DataContract.Students.CHAPTER +" LIKE ? ",
                new String[]{"%"+query+"%", "%"+query+"%", "%"+query+"%",
                        "%"+query+"%", "%"+query+"%", "%"+query+"%"},
                DataContract.Students.SORT_ORDER_DEFAULT
                );
                */

        return c;
    }


    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
