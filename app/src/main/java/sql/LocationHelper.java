package sql;

/*
  Created by Angel on 06.02.2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import modal.Locations;

public class LocationHelper extends SQLiteOpenHelper {
    private static LocationHelper instance;

    public static synchronized LocationHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new LocationHelper(context);

        return instance;
    }

    // User Table Columns names
    public static final String COLUMN_LOCATION_ID = "id";
    public static final String COLUMN_URL = "url";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LocationManager.db";
    // User table name
    private static final String TABLE_LOCATIONS = "location";
    private static final String COLUMN_LOCATION = "locations";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_STATUS = "status";


    /**
     * Constructor
     *
     * @param context
     */
    public LocationHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table sql query
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS
                + "("
                + COLUMN_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_URL + " TEXT, "
                + COLUMN_STATUS + " INTEGER"

                + ")";
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        // drop table sql query
        String DROP_LOCATIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;
        db.execSQL(DROP_LOCATIONS_TABLE);

        // Create tables again
        onCreate(db);
    }

    /**
     * This method is to create user record
     *
     * @param locations
     */
    public void addLocation(Locations locations, String time, String url, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION, locations.getLocations());
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_STATUS, status);


        // Inserting Row
        db.insert(TABLE_LOCATIONS, null, values);
        db.close();

        // Return user
    }

//    /**
//     * This method is to fetch all user and return the list of user records
//     *
//     * @return list
//     */
    public List<Locations> getAllLocations() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_LOCATION_ID,
                COLUMN_LOCATION,
                COLUMN_TIME,
                COLUMN_URL,
                COLUMN_STATUS,

        };
        // sorting orders
        String sortOrder =
                COLUMN_LOCATION_ID + " ASC";
        List<Locations> locationsList = new ArrayList<Locations>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_LOCATIONS, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Locations locations = new Locations();
                locations.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION_ID))));
                locations.setLocations(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
                locations.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                locations.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                locations.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));


                // Adding user record to list
                locationsList.add(locations);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return locationsList;
    }


    public void deleteLocation(Locations locations) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by ID
        db.delete(TABLE_LOCATIONS, COLUMN_LOCATION_ID + " = ?",
                new String[]{String.valueOf(locations.getId())});
        db.close();
    }

    public void deleteLocationById(int locationID) {
        SQLiteDatabase db = this.getWritableDatabase();
//        // delete user record by ID
        db.delete(TABLE_LOCATIONS, COLUMN_LOCATION_ID + " = " + locationID, null);
        db.close();
    }

    private JSONArray getResults() {

        String myPath = "" + "LocationManager.db";// Set path to your database

        String myTable = "location";//Set name of your table

//or you can use `context.getDatabasePath("my_db_test.db")`

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet;
    }

    public String getStartTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_TIME + " FROM " + TABLE_LOCATIONS + " WHERE " + COLUMN_LOCATION_ID + " = 1";
        String startTime = "-1";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
            startTime = (c.getString(c.getColumnIndex("time")));
        }
        c.close();
        db.close();
        return startTime;
    }


    public Cursor getUnsyncedLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + COLUMN_STATUS + " = 1;";
        return db.rawQuery(sql, null);
    }


    public boolean updateTime(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIME, time);
        db.update(TABLE_LOCATIONS, contentValues, COLUMN_LOCATION_ID + "= 1", null);
        db.close();
        return true;
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_LOCATIONS, contentValues, COLUMN_LOCATION_ID + "= " + id, null);
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_LOCATIONS);
        db.close();
    }

    public void deleteAllButFirst() {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by ID
        db.delete(TABLE_LOCATIONS, COLUMN_LOCATION_ID + " > 1", null);
        db.close();
    }

    public boolean checkAllSynced() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE " + COLUMN_STATUS + " = 1;";
        Cursor cursor = db.rawQuery(sql, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount <= 0;
    }

}



