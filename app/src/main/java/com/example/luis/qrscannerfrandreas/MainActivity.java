package com.example.luis.qrscannerfrandreas;


import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

import BroadcastReceivers.NetworkStateChecker;
import BroadcastReceivers.UpdateAlarmReceiver;
import Services.LocationService;
import Services.TestJobService;
import Services.UpdateService;
import methods.GetMacAdress;
import methods.LocationTracker;
import methods.MyBounceInterpolator;
import methods.SendToServer;
import methods.TimeCalculator;
import modal.Locations;
import sql.DatabaseHelper;
import sql.LocationHelper;
import sql.WorkingFlagHelper;

import static android.Manifest.permission;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //Server
    public static final String URL_SAVE_NAME = "https://kuriere.seqlab.eu/getBoxlist4App.php?vers=" + BuildConfig.VERSION_NAME + "&";
    public static final String URL_SEND_SCAN = "https://kuriere.seqlab.eu/setAppScan.php?vers=" + BuildConfig.VERSION_NAME + "&";
    public static final String URL_SEND_SCAN_PATH = "https://kuriere.seqlab.eu/";
    public static final String URL_SEND_SCAN_VERS = "?vers=" + BuildConfig.VERSION_NAME + "&";
    public static final String URL_SEND_LOC = "https://kuriere.seqlab.eu/geotrack.php?vers=" + BuildConfig.VERSION_NAME + "&";
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "Daten gespeichert.";
    public static String URL_SAVE_SCAN = null;
    public static String  city;
    public static int boxlistID, boxID;
    private final UpdateAlarmReceiver alarm = new UpdateAlarmReceiver();
    public String poq, path, idStr;

    //Location
    Context mContext;
    boolean gpsEnabled;
    boolean networkEnabled;
    LocationManager locationManager;
    LocationTracker locationTracker;
    Locations locations;
    Location location;
    String addr;
    String lon;
    String lat;
    String alt;
    String acc;
    SendToServer sendToServer;
    NetworkStateChecker networkStateChecker;
    String provider;
    //View Objects
    private Button buttonScan, buttonShowSQL, buttonShowTime;
    private TextView textViewVersion;
    private Menu menu;
    //Time
    private Date theTime, timenow, expectedTime;
    private DateFormat df, dfDate, dfGetTime;
    private TimeCalculator timeCalculator;
    private String todaysDate;
    private int vibTime = 1500;
    //Helper
    private DatabaseHelper databaseHelper;
    private LocationHelper locationHelper;
    private WorkingFlagHelper workingFlagHelper;
    private TestJobService testJobService;
    private GetMacAdress getMacAdress;
    private BroadcastReceiver broadcastReceiver;
    LocationService locationService;
    Intent locationIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workingFlagHelper = new WorkingFlagHelper(this);
        //Initializing view objects
        buttonScan = findViewById(R.id.buttonScan);
        buttonShowSQL = findViewById(R.id.buttonShowSQL);
        textViewVersion = findViewById(R.id.textViewVersion);
        textViewVersion.setText("Vers. " + BuildConfig.VERSION_NAME);
        locationService = new LocationService(this);
        locationIntent = new Intent(this, locationService.getClass());
        //getLocation();

        if (workingFlagHelper.getWorkingStatus() == 1) {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_green_24dp);

            if (!isMyServiceRunning(locationService.getClass())) {
                startService(locationIntent);
            }

        } else if (workingFlagHelper.getWorkingStatus() == 0) {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_black_24dp);
        }

        //Initializing onclick listeners
        buttonScan.setOnClickListener(this);
        buttonShowSQL.setOnClickListener(this);
        buttonShowTime.setOnClickListener(this);
        textViewVersion.setOnClickListener(this);

        //Initializing helpers
        locations = new Locations();
        databaseHelper = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);
        timeCalculator = new TimeCalculator();
        sendToServer = new SendToServer();
        testJobService = new TestJobService();
        getMacAdress = new GetMacAdress();
        mContext = this;
        locationTracker = new LocationTracker(this);
        alarm.setAlarm(this);



        //Broadcast
        registerReceiver(new NetworkStateChecker(),new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //Permission for location?
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    public void changeBGColor(int color) {
        RelativeLayout activityMain = findViewById(R.id.activity_main);
        if (color == 1) {
            activityMain.setBackgroundColor(RED);
        } else if (color == 2) {
            activityMain.setBackgroundColor(GREEN);
        } else if (color == 3) {
            activityMain.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.colorgradient, null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getLocation();
        if (workingFlagHelper.getWorkingStatus() == 1) {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_green_24dp);
            if (!isMyServiceRunning(locationService.getClass())) {
                startService(locationIntent);
            }
        } else if (workingFlagHelper.getWorkingStatus() == 0) {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_black_24dp);
        }
    }

    //Getting the scan results

    @Override
    protected void onRestart() {
        super.onRestart();
        //getLocation();

        if (workingFlagHelper.getWorkingStatus() == 1) {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_green_24dp);
            if (!isMyServiceRunning(locationService.getClass())) {
                startService(locationIntent);
            }
        } else {
            buttonShowTime = findViewById(R.id.buttonShowTime);
            buttonShowTime.setBackgroundResource(R.drawable.ic_timer_black_24dp);
            if (isMyServiceRunning(locationService.getClass())) {
                stopService(locationIntent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (workingFlagHelper.getWorkingStatus() == 1) {

            if (!isMyServiceRunning(locationService.getClass())) {
                startService(locationIntent);
            }

        } else if (workingFlagHelper.getWorkingStatus() == 0) {
            if (isMyServiceRunning(locationService.getClass())) {
                stopService(locationIntent);
            }
        }
        super.onDestroy();
    }

    //Disable Back-button
    @Override
    public void onBackPressed() {
        return;
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        try {

            //Permission for location?
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, 101);

            }else{

                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                //if QR-Code has nothing in it
                if (result.getContents() == null) {

                    Toast.makeText(this, getString(R.string.error_scan), Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle("Fehler")
                            .setMessage("Scanvorgang misslungen. Bitte wiederholen!")
                            .setIcon(R.drawable.ic_error_red_24dp)
                            .setNeutralButton("Ok", null)
                            .show();

                    changeBGColor(1);
                    vib.vibrate(vibTime);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            changeBGColor(3);
                        }
                    }, 15000);


                } else {

                    df = new SimpleDateFormat("HH:mm:ss");

                    timenow = Calendar.getInstance().getTime();

                    poq = "0";


                    Uri uri = Uri.parse(result.getContents());

                    if (!uri.getBooleanQueryParameter("poq", false)) {
                        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                .setTitle("Fehler")
                                .setMessage("Keine Postkastennummer im QR-Code, richtigen Code erwischt?.")
                                .setIcon(R.drawable.ic_error_red_24dp)
                                .setNeutralButton("Ok", null)
                                .show();

                        changeBGColor(1);
                        vib.vibrate(vibTime);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                changeBGColor(3);
                            }
                        }, 15000);

                    } else {
                        path = uri.getPath();
                        idStr = path.substring(path.lastIndexOf('/') + 1);
                        Log.i("PATH debug", path + "\n" + idStr);
                        poq = uri.getQueryParameter("poq");
                        boxID = Integer.parseInt(poq);

                        city = databaseHelper.getCity(boxID);




                            dfDate = new SimpleDateFormat("dd.MM.yyyy");
                            todaysDate = dfDate.format(Calendar.getInstance().getTime());
                            dfGetTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");




                        try {
                                expectedTime = dfGetTime.parse(todaysDate + " " + databaseHelper.getTime(boxID));

                            } catch (ParseException e) {
                                e.printStackTrace();

                            }


                            df = new SimpleDateFormat("HH:mm:ss");
                            theTime = Calendar.getInstance().getTime();

                                String scanToSend = URL_SEND_SCAN + "boxid=" + poq + "&" + "mac=" + GetMacAdress.getMacAddr() + "&" + "lat=" +locationTracker.lat + "&" + "lon=" + locationTracker.lon + "&" + "acc=" + locationTracker.acc + "&" + "sig=" + locationTracker.provider + "&" + "toc="; //+ df.format(theTime);
                                saveScanToServer(scanToSend, theTime, 2);


                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }}
        } catch (UnsupportedOperationException e) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Fehler")
                    .setMessage("Kein gültiger QR-Code.")
                    .setIcon(R.drawable.ic_error_red_24dp)
                    .setNeutralButton("Ok", null)
                    .show();

            vib.vibrate(vibTime);

            changeBGColor(1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeBGColor(3);
                }
            }, 15000);


        } catch (Exception e) {
            new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle("Fehler")
                    .setMessage("Kein gültiger QR-Code.")
                    .setIcon(R.drawable.ic_error_red_24dp)
                    .setNeutralButton("Ok", null)
                    .show();

            vib.vibrate(vibTime);

            changeBGColor(1);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeBGColor(3);
                }
            }, 15000);


        }
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

    @Override
    public void onClick(View v) {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        switch (v.getId()) {
            case R.id.buttonScan:

                //Permission for location?
                if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, 101);
                }else{

                if (workingFlagHelper.getWorkingStatus() == 0) {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setTitle("Sie haben Ihre Arbeit noch nicht begonnen!")
                            .setMessage("Arbeit jetzt beginnen?")
                            .setIcon(R.drawable.ic_access_time_red_24dp)
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent tourIntent = new Intent(getApplicationContext(), SelectCityActivity.class);
                                    startActivity(tourIntent);
                                }
                            })
                            .setNegativeButton("Nein", null)
                            .show();
                    vib.vibrate(vibTime);

                } else {
                    if (!databaseHelper.checkIfBoxlistExists()) {
                        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                .setTitle("Fehler")
                                .setMessage("Boxenliste konnte noch nicht aktualisiert werden." + "\n" + "\n" + "Aktuelle Liste wird jetzt geladen.")
                                .setIcon(R.drawable.ic_assignment_black_24dp)
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        syncBoxes();
                                    }
                                })
                                .show();

                    } else {
                        new IntentIntegrator(MainActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
                    }
                }}

                break;

            case R.id.buttonShowSQL:

                Intent scansIntent = new Intent(getApplicationContext(), TourExpListActivity.class);
                startActivity(scansIntent);
                //loadScans();
                break;

            case R.id.buttonShowTime:

                Intent tourIntent = new Intent(getApplicationContext(), SelectCityActivity.class);
                startActivity(tourIntent);
                break;

            case R.id.textViewVersion:
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.05, 40);
                myAnim.setInterpolator(interpolator);
                textViewVersion.startAnimation(myAnim);
                Intent serviceUpdate = new Intent(this, UpdateService.class);
                startService(serviceUpdate);
                break;


        }
    }


    /**
     * -----------------------------------------SQLite/Server Methods-----------------------------------------
     */

    /*
    * this method is passing the scan to the server
    * */
    private void saveScanToServer(String result, final Date time, final int timing) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final Date timeNow = Calendar.getInstance().getTime();

        progressDialog.setMessage("Saving Scan...");
        progressDialog.show();

        final String scan = result + time.getTime() / 1000;
        URL_SAVE_SCAN = scan + "&" + "tos=" + time.getTime() / 1000;


        Log.i("URLCALL debug", "Send URL: " + URL_SAVE_SCAN + "\n" + "mainActivity.saveScanToServer()");

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_SCAN,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("list");


                            if (obj.getInt("error") == 0) {
                                //obj.get("boxid");
                                //if there is a success
                                //storing the scan to sqlite with status synced
                                //databaseHelper.updateScanStatus(boxID, 2, scan);
                                //databaseHelper.updateScanDate(boxID, df.format(time));
                                //databaseHelper.updateScanDate2(boxID, df.format(time));
                                //databaseHelper.updateScanTiming(boxID, timing);
                                //databaseHelper.deleteFull();
                                databaseHelper.deleteFull();
                                databaseHelper = new DatabaseHelper(MainActivity.this);
                                Log.i("SCAN debug", "Liste gelöscht und neu erstellt");


                                for (int i = 0; i < arr.length(); i++) {
                                    // Get JSON object
                                    JSONObject jsonobj = arr.getJSONObject(i);
                                    //System.out.println(jsonobj.get("titel"));
                                    // Add boxID/Name extracted from Object

                                    //databaseHelper.addScan(Integer.valueOf(jsonobj.get("boxid").toString()), timeNow.getTime(), jsonobj.get("city").toString(), Integer.valueOf(jsonobj.get("cityID").toString()), Integer.valueOf(jsonobj.get("boxlistID").toString()), Integer.valueOf(jsonobj.get("nr_in_route").toString()), jsonobj.get("titel").toString(), jsonobj.get("street").toString(), jsonobj.get("insti").toString(), jsonobj.get("genau").toString(), jsonobj.get("nicht_vor").toString(), jsonobj.get("zeit").toString(), "-", 0, 0, 0L,0L,"");
                                    //databaseHelper.addScan(jsonobj.get("titel").toString(),2);

                                    String zeit = "-";
                                    int status = 0;

                                    if (!jsonobj.get("zeit").equals(null)){
                                        zeit = jsonobj.get("zeit").toString();
                                        status = 2;
                                    }

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


                                    Log.i("BOXUPDATE debug", "Boxenupdate erfolgreich, Box: " + jsonobj.get("boxid").toString() + " ist gespeichert");
                                    progressDialog.dismiss();

                                }

                                Log.i("URLCALL debug", "Box " + poq + " saved and synced (MainActivity).");
                                if (obj.getString("message") != "" ){

                                    new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                                            .setTitle("Serverantwort")
                                            .setMessage(obj.getString("message"))
                                            .setIcon(R.drawable.ic_assignment_black_24dp)
                                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent scansIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                                                    startActivity(scansIntent);
                                                }
                                            })
                                            .show();
                                }else{
                                Intent scansIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                                startActivity(scansIntent);}

                            } else {
                                //if there is some error
                                //saving the scan to sqlite with status unsynced
                                databaseHelper.updateScanStatus(boxID, 1, scan);
                                databaseHelper.updateScanDate(boxID, df.format(time));
                                databaseHelper.updateScanTiming(boxID, timing);

                                Log.i("URLCALL debug", "Box " + boxID + " saved unsynced (MainActivity)" + "\n" + "error : " + obj.get("error"));
                                if (obj.getString("message") != "" ){

                                    new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                                            .setTitle("Serverantwort")
                                            .setMessage(obj.getString("message"))
                                            .setIcon(R.drawable.ic_assignment_black_24dp)
                                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent scansIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                                                    startActivity(scansIntent);
                                                }
                                            })
                                            .show();}else{
                                    Intent scansIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                                    startActivity(scansIntent);}

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        if ( !databaseHelper.checkScanExist(poq)) {
                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                            new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle)
                                    .setTitle("Fehler")
                                    .setMessage("Gerade besteht keine Verbindung zum Server. " + "\n" + "Postkasten Nummer " + poq + " kann in der Offline-Liste nicht gefunden werden. ")
                                    .setIcon(R.drawable.ic_error_red_24dp)
                                    .setNeutralButton("Ok", null)
                                    .show();
                            changeBGColor(1);
                            vib.vibrate(vibTime);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    changeBGColor(3);
                                }
                            }, 15000);
                        }else{
                            final long diff = expectedTime.getTime() - timenow.getTime();
                            Log.i("URLCALL", "!!!" + "\n" + "!!!" + "\n" + diff + "\n" + "!!!" + "\n" + "!!!");

                            df = new SimpleDateFormat("HH:mm:ss");
                            theTime = Calendar.getInstance().getTime();


                            if (diff > 0) {
                                changeBGColor(1);
                                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle)
                                        .setTitle("Zu früh!")
                                        .setMessage("Postkasten Nummer " + poq + " darf nicht vor " + databaseHelper.getTime(boxID) + " abgeholt werden." + "\n" + "Bitte in " + timeCalculator.formatTime(diff) + " wiederholen!" + "\n" + "Soll ein Timer für diese Uhrzeit gestartet werden?")
                                        .setIcon(R.drawable.ic_access_time_black_24dp)
                                        .setPositiveButton("Ja",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_TIMER);
                                                alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Probenabholung Postkastennummer: " + poq);
                                                alarmIntent.putExtra(AlarmClock.EXTRA_LENGTH, (int)diff/1000);
                                                startActivity(alarmIntent);
                                            }
                                        })
                                        .setNegativeButton("Nein", null)
                                        .show();
                            } else {
                                Toast.makeText(MainActivity.this, "QR-Code erfolgreich erkannt.", Toast.LENGTH_LONG).show();
                                changeBGColor(2);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeBGColor(3);
                                    }
                                }, 3000);

                            }
                        //on error storing the scan to sqlite with status unsynced
                        boxlistID = databaseHelper.getBoxlistid(boxID);
                        databaseHelper.updateTodayBoxListID(boxlistID);
                        databaseHelper.updateScanStatus(boxID, 1, scan + time.getTime() / 1000);
                        databaseHelper.updateScanDate(boxID, df.format(time));
                        databaseHelper.updateScanTiming(boxID, timing);
                        Intent scansIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                        startActivity(scansIntent);

                        }

                        Log.i("URLCALL debug", "Server did not respond. Box " + boxID + " saved and added to the RequestQueue (MainActivity).");
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void syncBoxes() {
        final Date timeNow = Calendar.getInstance().getTime();
        databaseHelper.deleteFull();
        databaseHelper = new DatabaseHelper(this);
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Boxen werden geladen...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        final String URL_SAVE_BOXES = MainActivity.URL_SAVE_NAME + "mac=" + GetMacAdress.getMacAddr() + "&" + "tob=" + timeNow.getTime() / 1000;
        Log.i("BOXUPDATE debug", "!!!" + "\n" + "!!!" + "\n" + URL_SAVE_BOXES + "\n" + "!!!" + "\n" + "!!!");

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, URL_SAVE_BOXES,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("list");

                            if (arr.length() == 0) {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                                        .setMessage("Der Server liefert zur Zeit keine Liste mit Briefkästen.")
                                        .setIcon(R.drawable.ic_assignment_black_24dp)
                                        .setNeutralButton("Ok", null)
                                        .show();
                            } else {
                                for (int i = 0; i < arr.length(); i++) {
                                    // Get JSON object
                                    JSONObject jsonobj = arr.getJSONObject(i);
                                    //System.out.println(jsonobj.get("titel"));
                                    // Add boxID/Name extracted from Object                                    float lat;
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
                                    //databaseHelper.addScan(jsonobj.get("titel").toString(),2);
                                    Log.i("BOXUPDATE debug", "Boxenupdate erfolgreich, Box: " + jsonobj.get("boxid").toString() + " ist gespeichert");
                                    progressDialog.dismiss();

                                }
                            }

                        } catch (JSONException e) {
                            Log.e("BOXUPDATE debug", "unexpected JSON exception", e);
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        progressDialog.dismiss();
                        Log.e("BOXUPDATE debug", "Error waiting for the server to Respond.");

                        new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle)
                                .setTitle("Fehler")
                                .setMessage("Internetverbindung fehlt, Update nicht möglich!" + "\n" + "Sobald eine Verbindung besteht wird die akuelle Liste geladen." + "\n" + "Durch einen klick auf 'Einstellungen' können Sie die aktuellen Netzwerk-Einstellungen überprüfen.")
                                .setIcon(R.drawable.ic_assignment_red_24dp)
                                .setPositiveButton("Einstellungen",         new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("Abrechen",null)
                                .show();
                        vib.vibrate(vibTime);

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


}




