package sql;

/**
 * Created by Angel on 06.02.2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UrlHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "url.db";

    // User table name
    private static final String TABLE_URL = "url";

    // User Table Columns names

    private static final String COLUMN_URL_ID = "id";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_VERSION = "version";
    private static final String COLUMN_DATE = "date";


    // create table sql query
    private String CREATE_URL_TABLE = "CREATE TABLE " + TABLE_URL
            + "("
            + COLUMN_URL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_URL + " VARCHAR, "
            + COLUMN_VERSION + " VARCHAR, "
            + COLUMN_DATE + " INTEGER"

            + ")";


    // drop table sql query
    private String DROP_URL_TABLE = "DROP TABLE IF EXISTS " + TABLE_URL;

    /**
     * Constructor
     *
     * @param context
     */
    public UrlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_URL_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_URL_TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * This method is to create user record
     */
    public boolean newUrl(String url, String version) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(COLUMN_WORKING_ID,1);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_VERSION, version);
        values.put(COLUMN_DATE, 0L);


        // Inserting Row
        db.insert(TABLE_URL, null, values);
        db.close();

        return true;
    }

    public boolean updateDate(Long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_URL + " SET " + COLUMN_DATE + " = " + date + " WHERE " + COLUMN_URL_ID + " =1";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public boolean updateUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_URL + " SET " + COLUMN_URL + " = " + "'" + url + "'" + " WHERE " + COLUMN_URL_ID + " =1";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public boolean updateVersion(String version) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_URL + " SET " + COLUMN_VERSION + " = " + "'" + version + "'" + " WHERE " + COLUMN_URL_ID + " =1";
        db.execSQL(sql);
        db.close();
        return true;
    }

    public String getUrl() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_URL + " FROM " + TABLE_URL + " WHERE " + COLUMN_URL_ID + " = 1";
        String url = null;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            url = (c.getString(c.getColumnIndex("url")));
        }
        c.close();
        db.close();
        return url;
    }

    public String getVersion() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_VERSION + " FROM " + TABLE_URL + " WHERE " + COLUMN_URL_ID + " = 1";
        String version = null;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            version = (c.getString(c.getColumnIndex("version")));
        }
        c.close();
        db.close();
        return version;
    }

    public Long getDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_DATE + " FROM " + TABLE_URL + " WHERE " + COLUMN_URL_ID + " = 1";
        Long date = null;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            date = (c.getLong(c.getColumnIndex("date")));
        }
        c.close();
        db.close();
        return date;
    }


    public void deleteFull() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_URL_TABLE);
        db.execSQL(CREATE_URL_TABLE);
    }

}



