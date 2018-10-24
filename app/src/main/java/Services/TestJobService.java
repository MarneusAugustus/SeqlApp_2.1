package Services;

/**
 * Created by Angel on 20.02.2018.
 */

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.MainActivity;
import com.example.luis.qrscannerfrandreas.SelectCityActivity;
import com.example.luis.qrscannerfrandreas.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import methods.GetMacAdress;
import methods.TimeCalculator;
import sql.DatabaseHelper;
import sql.WorkingFlagHelper;
import utilities.Util;

/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */
public class TestJobService extends JobService {
    private static final String TAG = "SyncService";
    DatabaseHelper databaseHelper;
    TimeCalculator timeCalculator;
    GetMacAdress getMacAdress;
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    WorkingFlagHelper workingFlagHelper;
    Date currentTime = Calendar.getInstance().getTime();
    Date todayAtFour, todayAtMidnight, tidDate, boxDate;
    Date todaysDate;
    Date workingDate;
    LocationService locationService;
    Intent locationIntent;
    App app;
    SelectCityActivity selectCityActivity;

    @Override
    public boolean onStartJob(JobParameters params) {

        workingFlagHelper = new WorkingFlagHelper(this);
        databaseHelper = new DatabaseHelper(this);
        app = new App();
        locationService = new LocationService(this);
        locationIntent = new Intent(this, locationService.getClass());
        selectCityActivity = new SelectCityActivity();

        /**
         * -----------------------------------------------------------------------------------------------------------------------------------
         */

        /**
         * This method automatically updates the BoxList
         */

        SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
        String todaysDateString = dfDate.format(Calendar.getInstance().getTime());

        String boxDateString = dfDate.format(databaseHelper.getToday());
        Log.i("TID debug", "Last Boxupdate is from: " + boxDateString + "\n" + "Today is: " + todaysDateString);
        SimpleDateFormat dfGetTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            todaysDate = dfGetTime.parse(todaysDateString + " " + "04:01:00");
            boxDate = dfGetTime.parse(boxDateString + " " + "04:00:00");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        syncBoxes();


        /**
         * -----------------------------------------------------------------------------------------------------------------------------------
         */


        /**
         * Method to automatically update Workflag to 0 if it is 04:00am the next day.
         */

        //Checking if "Working Flag" is set on 1 or 0 and if it is actual.
        final String workingDateString = dfDate.format(workingFlagHelper.getDate());
        Log.i("WORKING debug", "Last working flag is from: " + workingDateString + "\n" + "Today is: " + todaysDateString);
        try {
            todaysDate = dfGetTime.parse(todaysDateString + " " + "04:01:00");
            workingDate = dfGetTime.parse(workingDateString + " " + "04:00:00");
            Log.i("WORKING debug", "Is it NOW later than " + dfGetTime.format(workingDate.getTime() + 86400000L) + " ?");


        } catch (ParseException e) {
            e.printStackTrace();
        }
        //if (workingDate.getTime() < todaysDate.getTime()) {
        if ((workingDate.getTime() + 86400000L) < todaysDate.getTime()) {

            workingFlagHelper.updateTime(0L);
            workingFlagHelper.updateWork(0, currentTime.getTime());


            //workingFlagHelper.updateWork(1, currentTime.getTime());
            Log.i("WORKING debug", "Yes it is. Workingflag set to 0, LocationService will stop.");
        }

        if (workingFlagHelper.getWorkingStatus() == 1) {

            if (!isMyServiceRunning(locationService.getClass())) {
                startService(locationIntent);
                Log.i("WORKING debug", "Workingflag set to 1, LocationService will start.");

            }

        } else if (workingFlagHelper.getWorkingStatus() == 0) {
            if (isMyServiceRunning(locationService.getClass())) {
                stopService(locationIntent);
                Log.i("WORKING debug", "Workingflag set to 0, LocationService will stop.");

            }
        }
            /**
             * -----------------------------------------------------------------------------------------------------------------------------------
             */


            Util.scheduleJob(getApplicationContext()); // reschedule the job
            return true;

        }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }



    public void syncBoxes() {
        final Date timeNow = Calendar.getInstance().getTime();
        databaseHelper.deleteFull();

        databaseHelper = new DatabaseHelper(this);

        final String URL_SAVE_BOXES = MainActivity.URL_SAVE_NAME + "mac=" + GetMacAdress.getMacAddr() + "&" + "tob=" + timeNow.getTime() / 1000;
        Log.i(BroadcastReceiver.class.getSimpleName(), "!!!" + "\n" + "!!!" + "\n" + URL_SAVE_BOXES + "\n" + "!!!" + "\n" + "!!!");

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_BOXES,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("list");

                            for (int i = 0; i < arr.length(); i++) {
                                // Get JSON object
                                JSONObject jsonobj = arr.getJSONObject(i);
                                float lon;
                                float lat;
                                try{
                                    lat = Float.valueOf(jsonobj.get("lat").toString());
                                }catch(NumberFormatException E){
                                    lat = 0;
                                }
                                try{
                                    lon = Float.valueOf(jsonobj.get("lon").toString());
                                }catch(NumberFormatException E){
                                    lon = 0;
                                }
                                databaseHelper.addScan(Integer.valueOf(jsonobj.get("boxid").toString()), timeNow.getTime(), jsonobj.get("city").toString(), Integer.valueOf(jsonobj.get("cityID").toString()), Integer.valueOf(jsonobj.get("boxlistID").toString()), Integer.valueOf(jsonobj.get("nr_in_route").toString()), jsonobj.get("titel").toString(), jsonobj.get("street").toString(), jsonobj.get("insti").toString(), jsonobj.get("genau").toString(), jsonobj.get("nicht_vor").toString(), "-", "-", 0, 0, lat,lon,Integer.valueOf(jsonobj.get("tourID").toString()),"");
                                //databaseHelper.addScan(jsonobj.get("titel").toString(),2);                                //databaseHelper.addScan(jsonobj.get("titel").toString(),2);
                                Log.i("BOXUPDATE debug", "Boxenupdate erfolgreich, Box: " + jsonobj.get("boxid").toString() + " ist gespeichert");

                            }

                        } catch (JSONException e) {
                            Log.e("BOXUPDATE debug", "unexpected JSON exception", e);
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("BOXUPDATE debug", "Error waiting for the server to Respond.");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("scan", URL_SAVE_BOXES);
                return params;
            }
        };


        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}