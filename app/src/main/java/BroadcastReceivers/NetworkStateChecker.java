package BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.luis.qrscannerfrandreas.MainActivity;
import com.example.luis.qrscannerfrandreas.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sql.DatabaseHelper;
import sql.LocationHelper;

import static com.example.luis.qrscannerfrandreas.MainActivity.URL_SAVE_SCAN;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper databaseHelper;
    private LocationHelper locationHelper;
    private Date theTime;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        databaseHelper = new DatabaseHelper(context);
        locationHelper = new LocationHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursorScan = databaseHelper.getUnsyncedScans();
                if (cursorScan.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to server
                        saveScan(
                                cursorScan.getInt(cursorScan.getColumnIndex(DatabaseHelper.COLUMN_BOXID)),
                                cursorScan.getString(cursorScan.getColumnIndex(DatabaseHelper.COLUMN_SCAN))
                        );
                    } while (cursorScan.moveToNext());
                }

                //getting all the unsynced locations
                Cursor cursorLoc = locationHelper.getUnsyncedLocations();
                if (cursorLoc.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveLoc(
                                cursorLoc.getInt(cursorLoc.getColumnIndex(LocationHelper.COLUMN_LOCATION_ID)),
                                cursorLoc.getString(cursorLoc.getColumnIndex(LocationHelper.COLUMN_URL))
                        );
                    } while (cursorLoc.moveToNext());
                }
            }
        }
    }

    /*
    * method taking two arguments
    * scan that is to be saved and id of the scan from SQLite
    * if the scan is successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveScan(final int id, final String scan) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        theTime = Calendar.getInstance().getTime();
        String currentTime = df.format(theTime);
        URL_SAVE_SCAN = scan + "&" + "tos=" + theTime.getTime() / 1000;
        Log.i("URLCALL debug", "Send URL: " + URL_SAVE_SCAN + "\n" + "Broadcast.saveScan()");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_SAVE_SCAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("error") == 0) {
                                //updating the status in sqlite
                                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                                theTime = Calendar.getInstance().getTime();
                                databaseHelper.updateScanStatus(id, 2, URL_SAVE_SCAN);
                                databaseHelper.updateScanDate2(id, df.format(theTime));
                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("scan", scan);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveLoc(final int id, final String scan) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        theTime = Calendar.getInstance().getTime();
        String currentTime = df.format(theTime);
        URL_SAVE_SCAN = scan;
        Log.i("URLCALL debug", "Send URL: " + URL_SAVE_SCAN + "\n" + "Broadcast.saveLoc()");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_SAVE_SCAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("error") == 0) {
                                locationHelper.updateStatus(id, 2);
                            } else {
                                Log.i("URLCALL debug", "Server responded with error: " + obj.getInt("error") + "\n" + "Broadcast.saveLoc()");
                                locationHelper.updateStatus(id, 2);
                                //locationHelper.deleteLocationById(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("scan", scan);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
