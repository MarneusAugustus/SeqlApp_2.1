package Services;

/*
  Created by Angel on 16.02.2018.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.SelectCityActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import methods.LocationTracker;
import modal.Locations;
import sql.LocationHelper;

public class LocationService extends Service {

    private Date startTimestamp;
    public int counter = 0;
    private Locations locations;
   private LocationTracker locationTracker;
   private DateFormat df;
    private Handler handler;
    private boolean running;
    SelectCityActivity selectCityActivity;
    private LocationHelper locationHelper;
    public LocationService(Context applicationContext) {
        super();
        if (App.debug == 1) {

            Log.i("SERVICE debug", "LocationService starts");
        }
        running = true;

        locations = new Locations();
    }

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locations = new Locations();
        locationHelper = new LocationHelper(this);
        locationTracker = new LocationTracker(this);
        df = new SimpleDateFormat("HH:mm:ss");
        startTimestamp = Calendar.getInstance().getTime();

        handler = new Handler();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        locationTracker.getLocation(10 * 1000, 0);

        //locationTracker.locationListener.onLocationChanged(locationTracker.getLocation(1 * 60 * 1000, 10));
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        locationTracker.locationListener.onLocationChanged(locationTracker.getLocation(0,1 ));
        locationTracker.stopTracking();
        if (locationHelper.checkAllSynced()) {
            locationHelper.deleteAll();
        }
        if (App.debug == 1) {

            Log.i("SERVICE debug", "LocationService onDestroy");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
