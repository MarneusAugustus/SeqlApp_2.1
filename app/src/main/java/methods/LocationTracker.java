package methods;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.example.luis.qrscannerfrandreas.MainActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import Services.LocationService;
import modal.Locations;
import sql.LocationHelper;

/**
 * Created by Angel on 25.02.2018.
 */

public class LocationTracker {

    public Date startTimestamp, timestamp;
    public Locations locations = new Locations();
    public LocationService locationService = new LocationService();
    public SendToServer sendToServer = new SendToServer();
    LocationManager locationManager;
    Location location;
    MainActivity mainActivity;
    boolean gpsEnabled;
    boolean networkEnabled;
    Context context;
    public static String addr, lon, lat, alt, acc, provider;
    public LocationHelper locationHelper = new LocationHelper(context);
    public LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {


            LocationHelper locationHelper = new LocationHelper(context);
            timestamp = Calendar.getInstance().getTime();

            if (location == null) {
                Log.i("LOCATION debug", "Currently, no locations can be provided.");
            } else {
                lon = String.valueOf(location.getLongitude());
                lat = String.valueOf(location.getLatitude());
                alt = String.valueOf(location.getAltitude());
                acc = String.valueOf(location.getAccuracy());
                provider = String.valueOf(location.getProvider());
                if (Float.valueOf(acc) < 25) {
                    sendToServer.saveLocationsToServer(lon,lat,acc,provider, MainActivity.URL_SEND_LOC, timestamp);
                    Log.i("LOCATION debug", "Saving location and sending it to server. The Accuracy has to be over 25m. Current Accuracy: " + acc + "m");

                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }
    };
    TimeCalculator timeCalculator;
    DateFormat df;



    public LocationTracker(Context context) {
        this.context = context;
    }

    public Location getLocation(int minTime, int minDistance) {


        try {
            if (locationManager == null) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }

            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            // stop if no providers are enabled
            if (!gpsEnabled && !networkEnabled) {

            }

            if (gpsEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
                if (location != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i("LOCATION debug", "Getting Location with GPS");
                } else {
                    Log.i("LOCATION debug", " location = null");
                }

            }

            if (networkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
                if (location != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.i("LOCATION debug", "Getting Location with GPS");
                } else {
                    Log.i("LOCATION debug", " location = null");
                }
            }


        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }

    public void stopTracking() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }
}
