package com.example.luis.qrscannerfrandreas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Services.LocationService;
import methods.LocationTracker;
import methods.MyBounceInterpolator;
import methods.SendToServer;
import methods.TimeCalculator;
import modal.Locations;
import sql.LocationHelper;
import sql.WorkingFlagHelper;


public class SelectCityActivity extends AppCompatActivity {

    private static Bundle bundle = new Bundle();
    public String tour;
    public boolean startIsChecked;
    public int buttonType;
    Intent mLocationIntent;
    Context ctx;
    ToggleButton start;
    Button buttonBack, stop, reset;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    DateFormat df, dfFull, dfDate;
    Date timestamp1;
    Date timestamp2;
    Date startTime;
    Date todaysDate;
    Date workingDate;
    Date todayAtFour;
    Locations locations;
    WorkingFlagHelper workingFlagHelper;
    MainActivity mainActivity;
    LocationHelper locationHelper;
    LocationTracker locationTracker;
    SendToServer sendToServer;
    String endTimer;
    TextView textView, textViewTime, textViewButton;
    Context mContext;
    Handler handler;
    int showWorkingFlag;
    long showTime;
    TimeCalculator timeCalculator;
    int Hours, Seconds, Minutes, MilliSeconds;
    String[] ListElements = new String[]{};
    List<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;
    private LocationService mLocationService;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        ctx = this;

        mLocationService = new LocationService(getCtx());
        mLocationIntent = new Intent(getCtx(), mLocationService.getClass());
        timeCalculator = new TimeCalculator();
        locations = new Locations();
        locationTracker = new LocationTracker(this);
        locationHelper = new LocationHelper(this);
        workingFlagHelper = new WorkingFlagHelper(this);
        sendToServer = new SendToServer();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        //View objects

        //Click listener
        textView = findViewById(R.id.textView);
        textViewTime = findViewById(R.id.textViewTime);
        textViewButton = findViewById(R.id.textViewButton);
        buttonBack = findViewById(R.id.buttonBack);
        df = new SimpleDateFormat("HH:mm");
        dfFull = new SimpleDateFormat();
        dfDate = new SimpleDateFormat("dd.MM.yy");

        if (workingFlagHelper.getWorkingStatus() == 1) {
            start = findViewById(R.id.start);
            start.setChecked(true);
            Log.i("WORKING debug", "Working Flag was set on 1.");
            textView.setText("Letzter Start: " + df.format(workingFlagHelper.getDate()) + "Uhr");
            try {
                timestamp1 = df.parse(df.format(workingFlagHelper.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            textViewButton.setText("Arbeit beenden");
            if (!isMyServiceRunning(mLocationService.getClass())) {
                startService(mLocationIntent);
            }
        } else {
            start = findViewById(R.id.start);
            start.setChecked(false);
            Log.i("WORKING debug", "Working Flag was set on 0.");
        }

        if (workingFlagHelper.getTime() != 0) {
            textViewTime.setText("Gesamtzeit vom " + dfDate.format(workingFlagHelper.getDate()) + "\n" + timeCalculator.formatTime(workingFlagHelper.getTime()));

        }

        handler = new Handler();

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(SelectCityActivity.this,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList

        );


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.isChecked()) {
                    final Animation myAnim = AnimationUtils.loadAnimation(ctx, R.anim.bounce);
                    // Use bounce interpolator with amplitude 0.2 and frequency 20
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 10);
                    myAnim.setInterpolator(interpolator);
                    start.startAnimation(myAnim);
                    Log.i("SELECTCITY debug", "ToggleButton  has been started.");
                    timestamp1 = Calendar.getInstance().getTime();
                    textView.setText("Letzter Start: " + df.format(timestamp1) + "Uhr");
                    textViewButton.setText("Arbeit beenden");


                    //workingFlagHelper.deleteFull();
                    //Log.i("WORKING debug", "Working Flag Database was deleted");
                    //workingFlagHelper = new WorkingFlagHelper(ctx);
                    workingFlagHelper.updateWork(1, timestamp1.getTime());
                    //workingFlagHelper.updateWork(1, timestamp1.getTime());
                    //workingFlagHelper.updateDate(timestamp1.getTime());
                    Log.i("WORKING debug", "Working Flag: " + workingFlagHelper.getWorkingStatus() + " Starttime: " + dfFull.format(workingFlagHelper.getDate()));


                    if (!isMyServiceRunning(mLocationService.getClass())) {
                        startService(mLocationIntent);
                    }

                } else {
                    final Animation myAnim = AnimationUtils.loadAnimation(ctx, R.anim.bounce);

                    // Use bounce interpolator with amplitude 0.2 and frequency 20
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 10);
                    myAnim.setInterpolator(interpolator);

                    start.startAnimation(myAnim);
                    builder
                            .setTitle("Arbeit wird beendet")
                            .setMessage("Sind Sie sicher?")
                            .setIcon(R.drawable.ic_timer_black_24dp)
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isMyServiceRunning(mLocationService.getClass())) {
                                        stopService(mLocationIntent);
                                    }
                                    //workingFlagHelper.updateWork(0);
                                    //Log.i("WORKING debug", "Working Flag: " +workingFlagHelper.getWorkingStatus() + " Starttime: " + dfFull.format(workingFlagHelper.getDate()));

                                    timestamp2 = Calendar.getInstance().getTime();

                                    //workingFlagHelper.deleteAll();
                                    //workingFlagHelper.startWork(0,timestamp2.getTime());
                                    /**
                                     workingFlagHelper.deleteFull();
                                     Log.i("WORKING debug", "Working Flag Database was deleted");
                                     //workingFlagHelper = new WorkingFlagHelper(ctx);
                                     workingFlagHelper.startWork(0,timestamp2.getTime());*/
                                    long diff = timestamp2.getTime() - timestamp1.getTime();

                                    workingFlagHelper.updateWork(0, timestamp2.getTime());
                                    workingFlagHelper.updateTime(workingFlagHelper.getTime() + diff);
                                    //workingFlagHelper.updateDate(timestamp1.getTime());
                                    Log.i("WORKING debug", "Working Flag: " + workingFlagHelper.getWorkingStatus() + " Starttime: " + dfFull.format(workingFlagHelper.getDate()));

                                    textViewTime.setText("Gesamtzeit vom " + dfDate.format(workingFlagHelper.getDate()) + "\n" + timeCalculator.formatTime(workingFlagHelper.getTime()));
                                    textView.setText("Arbeitsende: " + df.format(timestamp2) + "Uhr" + "\n" + "Gearbeitete Zeit: " + timeCalculator.formatTime(diff));
                                    textViewButton.setText("Arbeit starten");
                                }
                            })
                            .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            start.setChecked(true);
                                            if (!isMyServiceRunning(mLocationService.getClass())) {
                                                startService(mLocationIntent);
                                            }
                                            textView.setText("Letzter Start: " + df.format(timestamp1) + "Uhr");
                                            textViewButton.setText("Arbeit beenden");
                                        }
                                    }
                            )
                            .show();
                }
            }
        });

        /**
         if (start.isChecked()){
         mainActivity.getLocation();
         }*/

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });
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
    public void onPause() {
        super.onPause();
        /**
         if (start.isChecked()) {
         bundle.putBoolean("ToggleButtonState", start.isChecked());
         bundle.putLong("StartTime", timestamp1.getTime());
         }
         */
    }

    @Override
    public void onResume() {
        super.onResume();
        //start.setChecked(bundle.getBoolean("ToggleButtonState", false));
        if (workingFlagHelper.getWorkingStatus() == 1) {
            start.setChecked(true);
            timestamp1 = new Date(workingFlagHelper.getDate());
            //textView.setText("Schichtbeginn: " + df.format(timestamp1) + "Uhr");
            //textView.setText("Arbeitsbeginn: " + locationHelper.getStartTime() + "Uhr");
            textView.setText("Letzter Start: " + df.format(workingFlagHelper.getDate()) + "Uhr");

            /**
             try {
             timestamp1 = df.parse(locationHelper.getStartTime());
             } catch (ParseException e) {
             e.printStackTrace();
             }*/
            textViewButton.setText("Arbeit beenden");
        } else {
            start.setChecked(false);
            timestamp1 = new Date(workingFlagHelper.getDate());

            textViewButton.setText("Arbeit starten");
        }
        if (workingFlagHelper.getTime() != 0) {
            textViewTime.setText("Gesamtzeit vom " + dfDate.format(workingFlagHelper.getDate()) + "\n" + timeCalculator.formatTime(workingFlagHelper.getTime()));

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /**
         if (start.isChecked()) {
         bundle.putBoolean("ToggleButtonState", start.isChecked());
         bundle.putLong("StartTime", timestamp1.getTime());
         }
         */

    }

    @Override
    public void onRestart() {
        super.onRestart();
        //start.setChecked(bundle.getBoolean("ToggleButtonState", false));

        if (workingFlagHelper.getWorkingStatus() == 1) {
            start.setChecked(true);
            timestamp1 = new Date(workingFlagHelper.getDate());
            //textView.setText("Schichtbeginn: " + df.format(timestamp1) + "Uhr");
            //textView.setText("Arbeitsbeginn: " + locationHelper.getStartTime() + "Uhr");
            textView.setText("Letzter Start: " + df.format(workingFlagHelper.getDate()) + "Uhr");

            /**
             try {
             timestamp1 = df.parse(locationHelper.getStartTime());
             } catch (ParseException e) {
             e.printStackTrace();
             }*/
            textViewButton.setText("Arbeit beenden");
        } else {
            start.setChecked(false);
            timestamp1 = new Date(workingFlagHelper.getDate());
            textViewButton.setText("Arbeit starten");
        }
        if (workingFlagHelper.getTime() != 0) {
            textViewTime.setText("Gesamtzeit vom " + dfDate.format(workingFlagHelper.getDate()) + "\n" + timeCalculator.formatTime(workingFlagHelper.getTime()));

        }
    }

    @Override
    protected void onDestroy() {
        Log.i("SELECTCITY debug", "Activity's onDestroy.");
        if (workingFlagHelper.getWorkingStatus() == 1) {

            if (!isMyServiceRunning(mLocationService.getClass())) {
                startService(mLocationIntent);
            }

        } else if (workingFlagHelper.getWorkingStatus() == 0) {
            if (isMyServiceRunning(mLocationService.getClass())) {
                stopService(mLocationIntent);
            }
        }
        super.onDestroy();
    }

}


