package com.example.luis.qrscannerfrandreas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.UsersRecyclerAdapter;
import methods.GetMacAdress;
import methods.MyBounceInterpolator;
import modal.Scans;
import sql.DatabaseHelper;

public class ScanListActivity extends AppCompatActivity {

    Date forTitle;
    DateFormat dfForTitle;
    MainActivity mainActivity;
    String title;
    int todayBoxlistID;
    int boxlistid;
    int tourID;
    boolean firstScanDone;
    private AppCompatActivity activity = ScanListActivity.this;
    private AppCompatTextView textViewName;
    private AppCompatTextView scanListTitle;
    private ImageView imageViewStatus;
    private Button buttonBack, buttonBoxUpdate;
    private TextView textBoxUpdate;
    private RecyclerView recyclerViewUsers;
    private List<Scans> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
 //   private TourRecyclerAdapter tourRecyclerAdapter;
    private DatabaseHelper databaseHelper;
    private ArrayList cities;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        setContentView(R.layout.activity_users_list);
        initViews();
        initObjects();


        dfForTitle = new SimpleDateFormat("dd.MM.yy, HH:mm");

        if (databaseHelper.checkIfBoxlistExists()) {
        title = "Liste vom " + dfForTitle.format(databaseHelper.getToday()) + " Uhr";
        }else{
        title = "Keine aktuelle Liste geladen.";
        }

        getSupportActionBar().setTitle(title);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });

        buttonBoxUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 50);
                myAnim.setInterpolator(interpolator);
                buttonBoxUpdate.startAnimation(myAnim);
                if (databaseHelper.checkIfAlreadyScan()) {
                    new AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                            .setMessage("Die aktuelle Liste ist bereits geladen!")
                            .setIcon(R.drawable.ic_assignment_black_24dp)
                            .setNeutralButton("Ok", null)
                            .show();
                } else {
                    syncBoxes();

                }
            }
        });
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        imageViewStatus = findViewById(R.id.imageViewStatus);
        buttonBack = findViewById(R.id.buttonBack);
        buttonBoxUpdate = findViewById(R.id.ButtonBoxUpdate);
        textBoxUpdate = findViewById(R.id.textBoxUpdate);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        listUsers = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        databaseHelper = new DatabaseHelper(activity);
        todayBoxlistID = databaseHelper.getTodayBoxlistid();
        firstScanDone = databaseHelper.checkIfAlreadyScan();


        /**
         String todaysCity;
         if (MainActivity.city != null) {
         todaysCity = MainActivity.city;
         }else{
         todaysCity = dfForTitle.format(forTitle);
         }
         textViewName.setText("Tour: " + todaysCity);*/
       // tourID = tourRecyclerAdapter.tourID;
        boxlistid = databaseHelper.getBoxlistidFromTour(tourID);
        cities = new ArrayList<>();

        databaseHelper = new DatabaseHelper(activity);
        cities = databaseHelper.getCities(boxlistid);
        }

    @Override
    public void onBackPressed() {
        Intent tourIntent = new Intent(getApplicationContext(), TourListActivity.class);
        startActivity(tourIntent);
    }

    public void syncBoxes() {
        final Date timeNow = Calendar.getInstance().getTime();
        databaseHelper.deleteFull();
        //databaseHelper = new DatabaseHelper(this);
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Die neue Liste wird geladen...");
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
                                new AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                                        .setMessage("Der Server liefert zur Zeit keine Liste mit Briefkästen.")
                                        .setIcon(R.drawable.ic_assignment_black_24dp)
                                        .setNeutralButton("Ok", null)
                                        .show();
                            } else {
                                for (int i = 0; i < arr.length(); i++) {
                                    // Get JSON object
                                    JSONObject jsonobj = arr.getJSONObject(i);

                                    float lat;
                                    float lon;
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
                                    databaseHelper.addScan(Integer.valueOf(jsonobj.get("boxid").toString()), timeNow.getTime(), jsonobj.get("city").toString(), Integer.valueOf(jsonobj.get("cityID").toString()), Integer.valueOf(jsonobj.get("boxlistID").toString()), Integer.valueOf(jsonobj.get("nr_in_route").toString()), jsonobj.get("titel").toString(), jsonobj.get("street").toString(), jsonobj.get("insti").toString(), jsonobj.get("genau").toString(), jsonobj.get("nicht_vor").toString(), "-", "-", 0, 0, lat, lon,Integer.valueOf(jsonobj.get("tourID").toString()) ,"");
                                    //databaseHelper.addScan(jsonobj.get("titel").toString(),2);
                                    Log.i("BOXUPDATE debug", "Boxenupdate erfolgreich, Box: " + jsonobj.get("boxid").toString() + " ist gespeichert");
                                    progressDialog.dismiss();
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                    activity.startActivity(activity.getIntent());
                                    activity.overridePendingTransition(0, 0);

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
                        progressDialog.dismiss();
                        Log.e("BOXUPDATE debug", "Error waiting for the server to Respond.");

                        new AlertDialog.Builder(activity, R.style.AlertDialogStyle)
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("scan", URL_SAVE_BOXES);
                return params;
            }
        };


        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


    // This method is to fetch all user records from SQLite
    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    listUsers.clear();

                    /**
                    if (!firstScanDone) {
                        listUsers.addAll(databaseHelper.getAllScans());
                    } else {
                        listUsers.addAll(databaseHelper.getTodaysScans(todayBoxlistID));
                    }*/
                   listUsers.addAll(databaseHelper.getCities(boxlistid));


                } catch (Exception e) {
                    Log.e("ASYNC debug", "unexpected async exception", e);
                    Intent scanListIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                    startActivity(scanListIntent);
                    //Toast.makeText(App.getContext(),"Es ist ein Fehler aufgetreten",Toast.LENGTH_LONG);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}
