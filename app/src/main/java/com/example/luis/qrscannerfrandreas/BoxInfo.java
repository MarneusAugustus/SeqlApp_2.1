package com.example.luis.qrscannerfrandreas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import adapters.UsersRecyclerAdapter;
import methods.MyBounceInterpolator;
import sql.DatabaseHelper;

import static android.graphics.Color.WHITE;

public class BoxInfo extends AppCompatActivity {

    Button buttonBack, buttonMaps;
    TextView textViewBoxID, textViewBox, textViewInsti, textViewStreet, textViewGenau, textViewCity, textViewExpectedTime, textButton;
    LinearLayout linearLayout;
    DatabaseHelper databaseHelper;
    UsersRecyclerAdapter usersRecyclerAdapter;
    int boxid;
    int timing;
    int status;
    float lat;
    float lon;
    Uri boxLocation;
    boolean alreadyScaned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_info);
        boxid = usersRecyclerAdapter.boxid;
        initObjects();
        timing = databaseHelper.getTiming(boxid);
        status = databaseHelper.getStatus(boxid);
        lat = databaseHelper.getLati(boxid);
        lon = databaseHelper.getLongi(boxid);
        alreadyScaned = databaseHelper.checkIfAlreadyScan();
        boxLocation = Uri.parse("google.navigation:q=" + Float.toString(lat) + "," + Float.toString(lon));
        initViews();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(getApplicationContext(), ScanListActivity.class);
                startActivity(mainIntent);
            }
        });

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(BoxInfo.this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.05, 40);
                myAnim.setInterpolator(interpolator);
                buttonMaps.startAnimation(myAnim);
                new AlertDialog.Builder(BoxInfo.this, R.style.AlertDialogStyle)
                        .setMessage("Möchten Sie zum Standort der Box geführt werden?")
                        .setIcon(R.drawable.ic_directions_black_24dp)
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showMap(boxLocation);
                            }
                        })
                        .setNegativeButton("Nein", null)
                        .show();
            }
        });
    }

    private void initViews() {
        textViewBox = findViewById(R.id.textViewBox);
        textViewBox.setText(databaseHelper.getName(boxid));
        textViewBoxID = findViewById(R.id.textViewBoxID);
        textViewBoxID.setText(String.valueOf(boxid));
        textViewExpectedTime = findViewById(R.id.textViewExpectedTime);
        textViewExpectedTime.setText(databaseHelper.getTime(boxid));
        buttonBack = findViewById(R.id.buttonBack);
        if (lat != 0L) {
            buttonMaps = findViewById(R.id.buttonMaps);
            textButton = findViewById(R.id.textButton);
            buttonMaps.setVisibility(View.VISIBLE);
            textButton.setVisibility(View.VISIBLE);
        }else{
            buttonMaps = findViewById(R.id.buttonMaps);
            textButton = findViewById(R.id.textButton);
            buttonMaps.setVisibility(View.GONE);
            textButton.setVisibility(View.GONE);
        }
        textViewStreet = findViewById(R.id.textViewStreet);
        textViewStreet.setText(databaseHelper.getStreet(boxid));
        textViewGenau = findViewById(R.id.textViewGenau);
        textViewGenau.setText(databaseHelper.getGenau(boxid));
        textViewCity = findViewById(R.id.textViewCity);
        textViewCity.setText(databaseHelper.getCity(boxid));
        textViewInsti = findViewById(R.id.textViewInstitut);
        textViewInsti.setText(databaseHelper.getInsti(boxid));
        linearLayout = findViewById(R.id.linearLayoutID);
        if (alreadyScaned) {
            if (status == 1) {
                linearLayout.setBackgroundColor(Color.rgb(0xFE, 0xDE, 0x0A));
            } else if (status == 2) {
                linearLayout.setBackgroundColor(Color.rgb(0x00, 0xC0, 0x00));
            } else {
                linearLayout.setBackgroundColor(Color.rgb(0xc0, 0x00, 0x00));
                textViewBox.setTextColor(WHITE);
                textViewBoxID.setTextColor(WHITE);
                textViewCity.setTextColor(WHITE);
                textViewExpectedTime.setTextColor(WHITE);
                textViewStreet.setTextColor(WHITE);
                textViewInsti.setTextColor(WHITE);
                textViewGenau.setTextColor(WHITE);
            }

        } else {
            linearLayout.setBackgroundColor(Color.rgb(0x69, 0x69, 0x69));
            textViewBox.setTextColor(WHITE);
            textViewBoxID.setTextColor(WHITE);
            textViewCity.setTextColor(WHITE);
            textViewExpectedTime.setTextColor(WHITE);
            textViewStreet.setTextColor(WHITE);
            textViewInsti.setTextColor(WHITE);
            textViewGenau.setTextColor(WHITE);
        }
    }

    private void initObjects() {

        databaseHelper = new DatabaseHelper(this);
    }


    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

