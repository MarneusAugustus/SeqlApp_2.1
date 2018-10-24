package sql;

/**
 * Created by Angel on 20.11.2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import modal.Scans;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new DatabaseHelper(context);

        return instance;
    }

    // User Table Columns names
    public static final String COLUMN_SCAN_ID = "id";
    public static final String COLUMN_BOXID = "boxid";
    public static final String COLUMN_TODAY = "today";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_CITYID = "cityid";
    public static final String COLUMN_BOXLISTID = "boxlistid";
    public static final String COLUMN_TODAY_BOXLISTID = "todayboxlistid";
    public static final String COLUMN_ROUTEORDER = "routeorder";
    public static final String COLUMN_SCAN = "scan";
    public static final String COLUMN_EXPTIME = "exptime";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATESYN = "datesyn";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_INSTI = "insti";
    public static final String COLUMN_GENAU = "genau";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIMING = "timing";
    public static final String COLUMN_LON = "longitude";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_TOUR = "tourID";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ScanManager.db";
    // User table name
    private static final String TABLE_SCANS = "scans";
    // create table sql query
    private String CREATE_SCANS_TABLE = "CREATE TABLE " + TABLE_SCANS
            + " ("
            + COLUMN_SCAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_BOXID + " INTEGER, "
            + COLUMN_TODAY + " INTEGER, "
            + COLUMN_CITY + " VARCHAR, "
            + COLUMN_CITYID + " INTEGER, "
            + COLUMN_BOXLISTID + " INTEGER, "
            + COLUMN_TODAY_BOXLISTID + " INTEGER, "
            + COLUMN_ROUTEORDER + " INTEGER, "
            + COLUMN_NAME + " VARCHAR, "
            + COLUMN_STREET + " VARCHAR, "
            + COLUMN_INSTI + " VARCHAR, "
            + COLUMN_GENAU + " VARCHAR, "
            + COLUMN_EXPTIME + " VARCHAR, "
            + COLUMN_DATE + " VARCHAR, "
            + COLUMN_DATESYN + " VARCHAR, "
            + COLUMN_STATUS + " INTEGER, "
            + COLUMN_TIMING + " INTEGER, "
            + COLUMN_LON + " INTEGER, "
            + COLUMN_LAT + " INTEGER, "
            + COLUMN_TOUR + " INTEGER, "
            + COLUMN_SCAN + " VARCHAR);";

    //        + COLUMN_LOC + " TEXT"
    //       + ")";


    // drop table sql query
    private String DROP_SCANS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SCANS;

    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCANS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_SCANS_TABLE);

        // Create tables again
        onCreate(db);
    }

    /*
    * This method is taking two arguments
    * first one is the name that is to be saved
    * second one is the status
    * 0 means the name is synced with the server
    * 1 means the name is not synced with the server
    * */

    public boolean addScan(int boxid, long today, String city, int cityid, int boxlistid, int routeorder, String name, String street, String insti, String genau, String exptime, String time, String time2, int status, int timing, float latitude, float longitude, int tourID, String scan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_BOXID, boxid);
        contentValues.put(COLUMN_TODAY, today);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_CITYID, cityid);
        contentValues.put(COLUMN_BOXLISTID, boxlistid);
        contentValues.put(COLUMN_TODAY_BOXLISTID, 0);
        contentValues.put(COLUMN_ROUTEORDER, routeorder);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STREET, street);
        contentValues.put(COLUMN_INSTI, insti);
        contentValues.put(COLUMN_GENAU, genau);
        contentValues.put(COLUMN_EXPTIME, exptime);
        contentValues.put(COLUMN_DATE, time);
        contentValues.put(COLUMN_DATESYN, time2);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_TIMING, timing);
        contentValues.put(COLUMN_LAT, latitude);
        contentValues.put(COLUMN_LON, longitude);
        contentValues.put(COLUMN_TOUR, tourID);
        contentValues.put(COLUMN_SCAN, scan);

        db.insert(TABLE_SCANS, null, contentValues);
        //db.close();
        return true;
    }

    /**
     * This method is to fetch the whole SQLite and return the list of records
     *
     * @return list
     */

    public List<Scans> getAllScans() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SCAN_ID,
                COLUMN_BOXID,
                COLUMN_TODAY,
                COLUMN_CITY,
                COLUMN_CITYID,
                COLUMN_BOXLISTID,
                COLUMN_TODAY_BOXLISTID,
                COLUMN_ROUTEORDER,
                COLUMN_NAME,
                COLUMN_STREET,
                COLUMN_INSTI,
                COLUMN_GENAU,
                COLUMN_EXPTIME,
                COLUMN_DATE,
                COLUMN_DATESYN,
                COLUMN_STATUS,
                COLUMN_TIMING,
                COLUMN_LAT,
                COLUMN_LON,
                COLUMN_TOUR,
                COLUMN_SCAN
        };
        // sorting orders
        String sortOrder =
                COLUMN_SCAN_ID + " ASC";
        List<Scans> scanList = new ArrayList<Scans>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_SCANS, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Scans scan = new Scans(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXID)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITY)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITYID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROUTEORDER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STREET)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTI)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENAU)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPTIME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATESYN)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMING)),
                        cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAT)),
                        cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LON)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOUR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCAN)));

                // Adding user record to list
                scanList.add(scan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();


        // return user list
        return scanList;
    }

    /**
     * This method is to fetch the entries used today and return the list of records
     *
     * @return list
     */

    public List<Scans> getTodaysScans(int boxlistid) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SCAN_ID,
                COLUMN_BOXID,
                COLUMN_TODAY,
                COLUMN_CITY,
                COLUMN_CITYID,
                COLUMN_BOXLISTID,
                COLUMN_TODAY_BOXLISTID,
                COLUMN_ROUTEORDER,
                COLUMN_NAME,
                COLUMN_STREET,
                COLUMN_INSTI,
                COLUMN_GENAU,
                COLUMN_EXPTIME,
                COLUMN_DATE,
                COLUMN_DATESYN,
                COLUMN_STATUS,
                COLUMN_TIMING,
                COLUMN_LAT,
                COLUMN_LON,
                COLUMN_TOUR,
                COLUMN_SCAN
        };
        // sorting orders
        String sortOrder =
                COLUMN_CITYID + " , " + COLUMN_ROUTEORDER + " ASC";
        List<Scans> scanList = new ArrayList<Scans>();

        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_BOXLISTID + " = " + boxlistid;

        // query the user table

        Cursor cursor = db.query(TABLE_SCANS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Scans scan = new Scans(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXID)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITY)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITYID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROUTEORDER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STREET)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTI)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENAU)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPTIME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATESYN)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMING)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAT)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LON)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOUR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCAN)));

                // Adding user record to list
                scanList.add(scan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();


        // return user list
        return scanList;
    }

    public List<Scans> getSpecificTour(int tourid) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SCAN_ID,
                COLUMN_BOXID,
                COLUMN_TODAY,
                COLUMN_CITY,
                COLUMN_CITYID,
                COLUMN_BOXLISTID,
                COLUMN_TODAY_BOXLISTID,
                COLUMN_ROUTEORDER,
                COLUMN_NAME,
                COLUMN_STREET,
                COLUMN_INSTI,
                COLUMN_GENAU,
                COLUMN_EXPTIME,
                COLUMN_DATE,
                COLUMN_DATESYN,
                COLUMN_STATUS,
                COLUMN_TIMING,
                COLUMN_LAT,
                COLUMN_LON,
                COLUMN_TOUR,
                COLUMN_SCAN
        };
        // sorting orders
        String sortOrder =
                COLUMN_CITYID + " , " + COLUMN_ROUTEORDER + " ASC";
        List<Scans> scanList = new ArrayList<Scans>();

        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = COLUMN_BOXLISTID + " = " + tourid;

        // query the user table

        Cursor cursor = db.query(TABLE_SCANS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Scans scan = new Scans(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXID)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITY)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CITYID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TODAY_BOXLISTID)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROUTEORDER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STREET)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INSTI)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENAU)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPTIME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATESYN)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMING)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAT)),
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LON)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOUR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCAN)));

                // Adding user record to list
                scanList.add(scan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();


        // return user list
        return scanList;
    }

    public void deleteScan(int boxid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by ID
        db.delete(TABLE_SCANS, COLUMN_SCAN_ID + " = " + boxid,
                null);
        //db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SCANS);
        //db.close();
    }

    public void deleteFull() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_SCANS_TABLE);
        db.execSQL(CREATE_SCANS_TABLE);
    }

    /*
    * This method taking two arguments
    * first one is the id of the name for which
    * we have to update the sync status
    * and the second one is the status that will be changed
    * */
    public boolean updateScanStatus(int boxid, int status, String scan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_SCAN, scan);
        db.update(TABLE_SCANS, contentValues, COLUMN_BOXID + "=" + boxid, null);
        //db.close();
        return true;
    }


    public Long getToday() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TODAY + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_SCAN_ID + " = 1";
        Long date = 0L;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            date = (c.getLong(c.getColumnIndex("today")));
        }
        c.close();
        //db.close();
        return date;
    }

    public boolean updateScanTiming(int boxid, int timing) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIMING, timing);
        db.update(TABLE_SCANS, contentValues, COLUMN_BOXID + "=" + boxid, null);
        //db.close();
        return true;
    }

    public boolean updateScanDate(int boxid, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, time);
        db.update(TABLE_SCANS, contentValues, COLUMN_BOXID + "=" + boxid, null);
        //db.close();
        return true;
    }

    public boolean updateScanDate2(int boxid, String time2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATESYN, time2);
        db.update(TABLE_SCANS, contentValues, COLUMN_BOXID + "=" + boxid, null);
        //db.close();
        return true;

    }

    public boolean updateTodayBoxListID(int boxlistid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TODAY_BOXLISTID, boxlistid);
        db.update(TABLE_SCANS, contentValues, null, null);
        //db.close();
        return true;
    }


    /*
    * this method will give us all scans stored in sqlite
    * */
    public Cursor getScans() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " ORDER BY " + COLUMN_SCAN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * this method is for getting all the unsynced name
    * so that we can sync it with database
    * */
    public Cursor getUnsyncedScans() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " WHERE " + COLUMN_STATUS + " = 1;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor checkSyncedScans() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " WHERE " + COLUMN_STATUS + " = 2;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean checkScanExist(String postbox) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_SCAN_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_BOXID + " = ?";

        // selection arguments
        String[] selectionArgs = {postbox};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_SCANS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        //db.close();
        return cursorCount > 0;
    }

    public boolean checkAllSynced() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " WHERE " + COLUMN_STATUS + " < 2;";
        Cursor cursor = db.rawQuery(sql, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        //db.close();
        return cursorCount <= 0;
    }

    public boolean checkIfAlreadyScan() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " WHERE " + COLUMN_STATUS + " > 0;";
        Cursor cursor = db.rawQuery(sql, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        //db.close();
        return cursorCount > 0;
    }

    public boolean checkIfBoxlistExists() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCANS + " WHERE " + COLUMN_STATUS + " >= 0;";
        Cursor cursor = db.rawQuery(sql, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        //db.close();
        return cursorCount > 0;
    }

    public String getTime(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_EXPTIME + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String expectedTime = "Error";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            expectedTime = (c.getString(c.getColumnIndex("exptime")));
        }
        c.close();
        //db.close();
        return expectedTime;
    }


    public int getTiming(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TIMING + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        int timing = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            timing = (c.getInt(c.getColumnIndex("timing")));
        }
        c.close();
        //db.close();
        return timing;
    }

    public String getName(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_NAME + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String name = "Error";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            name = (c.getString(c.getColumnIndex("name")));
        }
        c.close();
        //db.close();
        return name;
    }

    public int getBoxlistid(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_BOXLISTID + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        int boxlistID = -0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            boxlistID = (c.getInt(c.getColumnIndex("boxlistid")));
        }
        c.close();
        //db.close();
        return boxlistID;
    }

    public int getBoxlistidFromTour(int tourID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_BOXLISTID + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_TOUR + " = " + tourID;
        int boxlistID = -0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            boxlistID = (c.getInt(c.getColumnIndex("boxlistid")));
        }
        c.close();
        //db.close();
        return boxlistID;
    }
    public int getTodayBoxlistid() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TODAY_BOXLISTID + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_SCAN_ID + " = 1";
        int todayboxlistID = -1;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            todayboxlistID = (c.getInt(c.getColumnIndex("todayboxlistid")));
        }
        c.close();
        //db.close();
        return todayboxlistID;
    }


    public String getCity(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_CITY + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String city = "Boxen-Liste";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            city = (c.getString(c.getColumnIndex("city")));
        }
        c.close();
        //db.close();
        return city;
    }

    public String getStreet(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_STREET + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String street = "Street";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            street = (c.getString(c.getColumnIndex("street")));
        }
        c.close();
        //db.close();
        return street;
    }

    public String getInsti(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_INSTI + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String insti = "Institut";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            insti = (c.getString(c.getColumnIndex("insti")));
        }
        c.close();
        //db.close();
        return insti;
    }

    public String getGenau(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_GENAU + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        String genau = "Genau";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            genau = (c.getString(c.getColumnIndex("genau")));
        }
        c.close();
        //db.close();
        return genau;
    }


    public int getStatus(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        int status = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            status = (c.getInt(c.getColumnIndex("status")));
        }
        c.close();
        //db.close();
        return status;
    }

    public float getLongi(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_LON + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        float lon = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            lon = (c.getFloat(c.getColumnIndex("longitude")));
        }
        c.close();
        //db.close();
        return lon;
    }

    public float getLati(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_LAT + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        float lat = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            lat = (c.getFloat(c.getColumnIndex("latitude")));
        }
        c.close();
        //db.close();
        return lat;
    }

    public int getTourID(int boxid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TOUR + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXID + " = " + boxid;
        int tourID = 0;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            tourID = (c.getInt(c.getColumnIndex("status")));
        }
        c.close();
        //db.close();
        return tourID;
    }

    public int getNumberOfTours() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT COUNT(DISTINCT " + COLUMN_TOUR + ") FROM " + TABLE_SCANS;
        int numberOfTours = 0;
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()){

        }
        numberOfTours = c.getCount();
        Log.i("SHOW TOURS debug", "Number of tours: "+numberOfTours +"\n" + "Coursor c: "+c + "\n" + "SQL query: " + sql);

       return numberOfTours;
    }

    public ArrayList getTours() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT " + COLUMN_TOUR + " FROM " + TABLE_SCANS + " ORDER BY "+ COLUMN_TOUR + " ASC";
        ArrayList<String> tours = new ArrayList<>();
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()){

            tours.add("Hauptstrecke " + c.getString(c.getColumnIndex("tourID")));
        }
        Log.i("SHOW TOURS debug", "Tours: "+tours +"\n" + "Coursor c: "+c + "\n" + "SQL query: " + sql);

        return tours;
    }




    public ArrayList getCities(int boxlistid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT " + COLUMN_CITY + " FROM " + TABLE_SCANS + " WHERE " + COLUMN_BOXLISTID + " = " + boxlistid;
        ArrayList<String> cities = new ArrayList<>();        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()){

            cities.add(c.getString(c.getColumnIndex("city")) + " ");
        }
        return cities;
    }

}

