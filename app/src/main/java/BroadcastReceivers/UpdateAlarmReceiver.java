package BroadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.luis.qrscannerfrandreas.App;

import java.util.Calendar;
import java.util.Date;

import sql.UrlHelper;


public class UpdateAlarmReceiver extends WakefulBroadcastReceiver {
    private UrlHelper urlHelper;
    Date todaysDate, lastUpdate;

    @Override
    public void onReceive(Context context, Intent intent) {
        urlHelper = new UrlHelper(App.getContext());
/*
 SimpleDateFormat dfDate = new SimpleDateFormat("dd.MM.yyyy");
 String todaysDateString = dfDate.format(Calendar.getInstance().getTime());

 String lastUpdateString = dfDate.format(urlHelper.getDate());
 if (App.debug == 1) {
 Log.i("UPDATE debug", "Last Update is from: " + lastUpdateString + "\n" + "Today is: " + todaysDateString);
 }
 SimpleDateFormat dfGetTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


 try {
 todaysDate = dfGetTime.parse(todaysDateString + " " + "04:01:00");
 lastUpdate = dfGetTime.parse(lastUpdateString + " " + "04:00:00");

 } catch (ParseException e) {
 e.printStackTrace();
 }
 if ((urlHelper.getDate()+86400000L) < todaysDate.getTime()) {

 // Intent service = new Intent(context, UpdateService.class);

 // Start the service, keeping the device awake while it is launching.
 //startWakefulService(context, service);
 }
 */

    }

    // BEGIN_INCLUDE(set_alarm)

    public void setAlarm(Context context) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();

        assert alarmMgr != null;
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60 * 1000, alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, UpdateBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

}