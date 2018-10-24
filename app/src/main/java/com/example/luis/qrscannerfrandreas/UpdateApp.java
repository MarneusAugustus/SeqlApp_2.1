package com.example.luis.qrscannerfrandreas;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import sql.DatabaseHelper;
import sql.UrlHelper;
import sql.WorkingFlagHelper;

/**
 * Created by Angel on 06.03.2018.
 */
public class UpdateApp extends Activity {
    boolean isDeleted;
    UrlHelper urlHelper;
    WorkingFlagHelper workingFlagHelper;
    DatabaseHelper databaseHelper;
    private BroadcastReceiver receiver;
    private long enqueue;
    private DownloadManager dm;

    private static boolean isRooted() {
        return findBinary("su");
    }

    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlHelper = new UrlHelper(this);
        workingFlagHelper = new WorkingFlagHelper(this);
        databaseHelper = new DatabaseHelper(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("QR-Scanner");
        builder.setIcon(R.mipmap.ic_fg_qr);
        builder.setMessage("Eine neue Version ist verfügbar." + "\n" + "Jetzt laden?");
        builder.getContext().setTheme(R.style.AppTheme_NoActionBar);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (haveStoragePermission()) {

                    Toast.makeText(getApplicationContext(), "Das Update wird geladen. Bitte warten", Toast.LENGTH_LONG).show();
                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                    File file = new File("/mnt/sdcard/Download/SeqlApp.apk");
                    if (file.exists()) {

                        isDeleted = file.delete();
                        deleteAndInstall();
                    } else {
                        firstTimeInstall();
                    }
                }
            }
        });
        builder.setNegativeButton("Später erinnern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateApp.this.finish();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });
        builder.show();
    }

    private void firstTimeInstall() {
        Log.d("UPDATE debug", "First Time? Or deleted from folder");
        downloadAndInstall();
    }

    private void deleteAndInstall() {
        if (isDeleted) {
            Log.d("UPDATE debug", "deleted file: " + String.valueOf(isDeleted));
            downloadAndInstall();

        } else {
            Log.d("UPDATE debug", "Not deleted: " + String.valueOf(isDeleted));
            Toast.makeText(getApplicationContext(), "Fehler beim updaten! Bitte später erneut versuchen", Toast.LENGTH_LONG).show();
        }
    }

    private void downloadAndInstall() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlHelper.getUrl()));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SeqlApp.apk");

        enqueue = dm.enqueue(request);


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    Toast.makeText(getApplicationContext(), "Download wurde ausgeführt", Toast.LENGTH_LONG).show();

                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            Log.d("UPDATE debug", "Successfull download: " + uriString);

                            if (downloadId == c.getInt(0)) {
                                Log.d("UPDATE debug", "Download path: " + c.getString(c.getColumnIndex("local_uri")));


                                Log.d("isRooted:", String.valueOf(isRooted()));
                                if (isRooted() == false) {
                                    //if your device is not rooted
                                    //File file = new File(Environment.getExternalStorageDirectory(), "SeqlApp.apk");

                                    Intent intent_install = new Intent(Intent.ACTION_VIEW);
                                    intent_install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent_install.setDataAndType(FileProvider.getUriForFile(UpdateApp.this,
                                            BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "SeqlApp.apk")), "application/vnd.android.package-archive");

                                    Log.d("UPDATE debug", "Enviroment.getExtStor(): " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "SeqlApp.apk");
                                    Toast.makeText(getApplicationContext(), "Installation startet gleich. Bitte warten.", Toast.LENGTH_LONG).show();
                                    startActivity(intent_install);
                                    unregisterReceiver(this);
                                    urlHelper.deleteFull();
                                    databaseHelper.deleteFull();
                                    workingFlagHelper.deleteFull();

                                } else {
                                    //if your device is rooted then you can install or update app in background directly
                                    Toast.makeText(getApplicationContext(), "Installation startet gleich. Bitte warten.", Toast.LENGTH_LONG).show();
                                    File file = new File("/sdcard/Download/SeqlApp.apk");
                                    Log.d("UPDATE debug", "installer: /sdcard/Download/SeqlApp.apk");
                                    if (file.exists()) {
                                        try {
                                            String command;
                                            Log.d("IN File exists:", "/mnt/sdcard/Download/SeqlApp.apk");

                                            command = "pm install -r " + "/mnt/sdcard/Download/SeqlApp.apk";
                                            Log.d("COMMAND:", command);
                                            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                                            proc.waitFor();
                                            Toast.makeText(getApplicationContext(), "Update erfolgreich installiert", Toast.LENGTH_LONG).show();
                                            urlHelper.updateDate(Calendar.getInstance().getTime().getTime());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    c.close();
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error", "You have permission");
                return true;
            } else {

                Log.e("Permission error", "You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}