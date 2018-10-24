package BroadcastReceivers;

/**
 * Created by Angel on 16.02.2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.luis.qrscannerfrandreas.App;

import Services.LocationService;
import sql.WorkingFlagHelper;

public class BroadcastService extends BroadcastReceiver {

    WorkingFlagHelper workingFlagHelper;
    LocationService locationService;
    App app;

    @Override
    public void onReceive(Context context, Intent intent) {
        app = new App();
        workingFlagHelper = new WorkingFlagHelper(App.getContext());
        locationService = new LocationService(App.getContext());

        Log.i("BROADCAST debug", "BroadcastReceiver detected Booting, check if working Flag is set on 1, and restart location.");
        if (workingFlagHelper.getWorkingStatus() == 1) {
            context.startService(new Intent(context, Services.LocationService.class));
            Log.i("BROADCAST debug", "Working Flag is set on 1. Restart LocationService.");

        }
    }


}