package methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.BuildConfig;
import com.example.luis.qrscannerfrandreas.MainActivity;
import com.example.luis.qrscannerfrandreas.SelectCityActivity;
import com.example.luis.qrscannerfrandreas.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Services.LocationService;
import modal.Locations;
import sql.DatabaseHelper;
import sql.LocationHelper;

/**
 * Created by Angel on 25.02.2018.
 */

public class SendToServer {

public static final String URL_SAVE_NAME = "https://kuriere.seqlab.eu/getBoxlist4App.php?toc=123456";
    public static final String URL_SEND_SCAN = "https://kuriere.seqlab.eu/setAppScan.php?vers=1.2.4&";
    private static String URL_SAVE_SCAN = null;
    private DatabaseHelper databaseHelper;
    private LocationHelper locationHelper;

    private MainActivity mainActivity;
    private Locations locations;
    public GetMacAdress getMacAdress;
    private App app;
    private SelectCityActivity selectCityActivity;
    private final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    Context context;
    double lon, lat, acc;
    String sig;
    Intent mLocationIntent;
    private LocationService mLocationService;



    /*
    * this method is passing the scan to the server
    * */

    public void saveScanToServer(String result, final Date time, final int timing) {

        app = new App();

        final String scan = result;
        URL_SAVE_SCAN = scan + time.getTime() / 1000 + "&" + "tos=" + time.getTime() / 1000;
        if (App.debug == 1) {

            Log.i("URLCALL debug", "URL-Request: " + URL_SAVE_SCAN + " is being created");
        }
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_SCAN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("error") == 0) {
                                //obj.get("boxid");
                                //if there is a success
                                //storing the scan to sqlite with status synced
                                databaseHelper.updateScanStatus(MainActivity.boxID, 2, scan);
                                databaseHelper.updateScanDate(MainActivity.boxID, df.format(time));
                                databaseHelper.updateScanDate2(MainActivity.boxID, df.format(time));
                                databaseHelper.updateScanTiming(MainActivity.boxID, timing);
                                if (App.debug == 1) {

                                    Log.i("URLCALL debug", "Box with ID: " + mainActivity.poq + " synced with server");
                                }
                            } else {
                                //if there is some error
                                //saving the scan to sqlite with status unsynced
                                databaseHelper.updateScanStatus(MainActivity.boxID, 1, scan);
                                databaseHelper.updateScanDate(MainActivity.boxID, df.format(time));
                                databaseHelper.updateScanTiming(MainActivity.boxID, timing);
                                if (App.debug == 1) {

                                    Log.i("URLCALL debug", "Box with ID: " + mainActivity.poq + " not synced with server. Server sent error response: " + obj.get("error"));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //on error storing the scan to sqlite with status unsynced
                        databaseHelper.updateScanStatus(MainActivity.boxID, 1, scan);
                        databaseHelper.updateScanDate(MainActivity.boxID, df.format(time));
                        databaseHelper.updateScanTiming(MainActivity.boxID, timing);
                        if (App.debug == 1) {

                            Log.i("URLCALL debug", "Error waiting for the server to respond.");
                        }
                    }
                });

        VolleySingleton.getInstance(app.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /**
     * Method to save the current location to the server as well as internal.
     *
     * @param serverurl
     * @param time
     */
    public void saveLocationsToServer(final String lon, final String lat, final String acc, final String sig, String serverurl, final Date time) {

        locations = new Locations();
        locationHelper = new LocationHelper(App.getContext());

        /**
        if (location == null) {
            lon = -1;
            lat = -1;
            acc = -1;
            sig = "noSignal";
        } else {
            lon = location.getLongitude();
            lat = location.getLatitude();
            acc = location.getAccuracy();
            sig = location.getProvider();
        }*/

        locations.setLocations("Longitude = " + lon + " \n" + "Latitude = " + lat + "\n" + "Accuracy = " + acc + " m)");

        URL_SAVE_SCAN = serverurl + "vers=" + BuildConfig.VERSION_NAME + "&" + "lon=" + lon + "&" + "lat=" + lat + "&" + "acc=" + acc + "&" + "tog=" + time.getTime() / 1000 + "&" + "mac=" + GetMacAdress.getMacAddr() + "&" + "sig=" + sig;
        if (App.debug == 1) {

            Log.i("URLCALL debug", "URL-Request: " + URL_SAVE_SCAN + " is being created");
        }
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_SCAN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            //JSONArray arr = obj.getJSONArray("error");

                            //JSONArray arr = obj.getJSONArray(response);

                            if (obj.getInt("error") == 0) {
                                if (App.debug == 1) {

                                    Log.i("URLCALL debug", "error = 0");
                                }
                                //locationHelper.addLocation(locations ,df.format(time),  URL_SAVE_SCAN,trackID ,2);
                            } else {
                                if (App.debug == 1) {

                                    Log.i("URLCALL debug", "error is not 0?" + "\n" + "error: " + obj.get("error").toString());
                                }
                                //locationHelper.addLocation(locations ,df.format(time),  URL_SAVE_SCAN,trackID ,1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (App.debug == 1) {

                                Log.i("URLCALL debug", "JSONEXCEPTION!");
                            }
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        locationHelper.addLocation(locations, df.format(time), URL_SAVE_SCAN, 1);
                        if (App.debug == 1) {

                            Log.i("URLCALL debug", "ERROR RESPONSE");
                        }
                    }
                });
        app = new App();
        selectCityActivity = new SelectCityActivity();
        VolleySingleton.getInstance(App.getContext()).addToRequestQueue(stringRequest);
        //VolleySingleton.getInstance(selectCityActivity.getApplicationContext()).addToRequestQueue(stringRequest);
        if (App.debug == 1) {

            Log.i("URLCALL debug", "Volley sending request...");
        }
    }

    public void sendBug(String serverurl, final Date time) {
        URL_SAVE_SCAN = serverurl + "tog=" + time.getTime() / 1000 + "&" + "mac=" + GetMacAdress.getMacAddr();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_SCAN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
    }
}
