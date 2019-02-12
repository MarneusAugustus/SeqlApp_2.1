package Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.MainActivity;
import com.example.luis.qrscannerfrandreas.UpdateApp;
import com.example.luis.qrscannerfrandreas.VolleySingleton;

import sql.UrlHelper;

public class DownloadService extends IntentService {

        App app;
        MainActivity mainActivity;

    public DownloadService() {
        super("DownloadService");
        mainActivity = new MainActivity();
        app = new App();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (App.debug == 1) {

            Log.i("UPDATE debug", "Service Download wurde gestartet!");
        }
        final Intent myIntent = new Intent(App.getContext(), UpdateApp.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final UrlHelper urlHelper = new UrlHelper(this);


        StringRequest newStringRequest = new StringRequest(com.android.volley.Request.Method.GET, urlHelper.getUrl(),
                response -> {
                    try {
                        if (App.debug == 1) {

                            Log.i("UPDATE debug", urlHelper.getUrl());
                        }
                        startActivity(myIntent);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Es ist ein Fehler aufgetreten!", Toast.LENGTH_LONG).show();

                    if (App.debug == 1) {

                        Log.i("UPDATE debug", "Error Response 2");
                    }
                });

        VolleySingleton.getInstance(App.getContext()).addToRequestQueue(newStringRequest);
        //VolleySingleton.getInstance(selectCityActivity.getApplicationContext()).addToRequestQueue(stringRequest);*/


    }
}

