package com.example.luis.qrscannerfrandreas;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import Services.LocationService;
import sql.UrlHelper;
import sql.WorkingFlagHelper;
import utilities.Util;

/**
 * Created by Angel on 21.02.2018.
 */


public class App extends Application {

    public static Context context;
    WorkingFlagHelper workingFlagHelper;
    UrlHelper urlHelper;


    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        workingFlagHelper = new WorkingFlagHelper(this);
        urlHelper = new UrlHelper(this);


        Log.i("SCHEDULER", "!!!" + "\n" + "!!!" + "\n" + "HERE IS WHERE THE MAGIC HAPPENS - JobScheduler" + "\n" + "!!!" + "\n" + "!!!");
        Util.scheduleJob(this);

        context = getApplicationContext();

        if (workingFlagHelper.getFlagname() == null) {
            workingFlagHelper.startWork(0, 0L);

        }

        if (urlHelper.getDate() == null) {
            urlHelper.newUrl(null, null);
        }
    }

}
