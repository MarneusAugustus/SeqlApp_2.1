package sql;

/**
 * Created by Angel on 06.02.2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkingFlagHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WorkingFlag.db";

    // User table name
    private static final String TABLE_WORKING = "working";

    // User Table Columns names

    private static final String COLUMN_WORKING_ID = "id";
    private static final String COLUMN_WORKING = "working";
    private static final String COLUMN_FLAGNAME = "flagname";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DATE = "date";


    // create table sql query
    private String CREATE_WORKING_TABLE = "CREATE TABLE " + TABLE_WORKING
            + "("
            + COLUMN_WORKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_WORKING + " INTEGER ,"
            + COLUMN_FLAGNAME + " VARCHAR ,"
            + COLUMN_TIME + " INTEGER ,"
            + COLUMN_DATE + " INTEGER"

            + ")";


    // drop table sql query
    private String DROP_WORKING_TABLE = "DROP TABLE IF EXISTS " + TABLE_WORKING;

    /**
     * Constructor
     *
     * @param context
     */
    public WorkingFlagHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKING_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_WORKING_TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * This method is to create user record
     *
     * @param working
     * @param date
     */
    public boolean startWork(int working, Long date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(COLUMN_WORKING_ID,1);
        values.put(COLUMN_WORKING, working);
        values.put(COLUMN_FLAGNAME, "workflag");
        values.put(COLUMN_TIME, 0L);
        values.put(COLUMN_DATE, date);


        // Inserting Row
        db.insert(TABLE_WORKING, null, values);
        db.close();

        return true;
    }


    public boolean updateWork(int working, Long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String sql = "UPDATE " + TABLE_WORKING + " SET " + COLUMN_WORKING + " = " + working + " , " + COLUMN_DATE + " = " + date + " WHERE " + COLUMN_FLAGNAME + " = 'workflag'";
        db.execSQL(sql);
        /**
         contentValues.put(COLUMN_WORKING, working);
         contentValues.put(COLUMN_DATE, date);
         db.update(TABLE_WORKING, contentValues, COLUMN_WORKING_ID + "= 1", null);*/
        db.close();
        return true;
    }

    public boolean updateTime(Long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String sql = "UPDATE " + TABLE_WORKING + " SET " + COLUMN_TIME + " = " + time + " WHERE " + COLUMN_FLAGNAME + " = 'workflag'";
        db.execSQL(sql);
        /**
         contentValues.put(COLUMN_WORKING, working);
         contentValues.put(COLUMN_DATE, date);
         db.update(TABLE_WORKING, contentValues, COLUMN_WORKING_ID + "= 1", null);*/
        db.close();
        return true;
    }

    public Long getTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TIME + " FROM " + TABLE_WORKING + " WHERE " + COLUMN_FLAGNAME + " = 'workflag'";
        Long time = 0L;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            time = (c.getLong(c.getColumnIndex("time")));
        }
        c.close();
        db.close();
        return time;
    }


    public boolean updateDate(long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        db.update(TABLE_WORKING, contentValues, COLUMN_WORKING_ID + "= 1", null);
        db.close();
        return true;
    }

    public int getWorkingStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_WORKING + " FROM " + TABLE_WORKING + " WHERE " + COLUMN_FLAGNAME + " = 'workflag'";
        int working = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            working = (c.getInt(c.getColumnIndex("working")));
        }
        c.close();
        db.close();
        return working;
    }

    public String getFlagname() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_FLAGNAME + " FROM " + TABLE_WORKING;
        String flagname = null;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            flagname = (c.getString(c.getColumnIndex("flagname")));
        }
        c.close();
        db.close();
        return flagname;
    }

    public Long getDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_DATE + " FROM " + TABLE_WORKING + " WHERE " + COLUMN_WORKING_ID + " = 1";
        Long date = 0L;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            date = (c.getLong(c.getColumnIndex("date")));
        }
        c.close();
        db.close();
        return date;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_WORKING);
        db.close();
    }

    public void deleteFull() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_WORKING_TABLE);
        db.execSQL(CREATE_WORKING_TABLE);
    }
}



