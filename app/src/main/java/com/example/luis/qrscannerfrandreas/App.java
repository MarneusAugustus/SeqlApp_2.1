package com.example.luis.qrscannerfrandreas;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import sql.UrlHelper;
import sql.WorkingFlagHelper;
import utilities.Util;

/**
 * Created by Angel on 21.02.2018.
 */


public class App extends Application {

    private static Context context;
    private WorkingFlagHelper workingFlagHelper;
    private UrlHelper urlHelper;
    public static int debug;


    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        workingFlagHelper = new WorkingFlagHelper(this);
        urlHelper = new UrlHelper(this);

        debug = 1;

        if (App.debug == 1) {
            Log.i("SCHEDULER", "!!!" + "\n" + "!!!" + "\n" + "HERE IS WHERE THE MAGIC HAPPENS - JobScheduler" + "\n" + "!!!" + "\n" + "!!!");
        }

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
