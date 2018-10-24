package Services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.BuildConfig;
import com.example.luis.qrscannerfrandreas.MainActivity;
import com.example.luis.qrscannerfrandreas.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import methods.GetMacAdress;
import sql.UrlHelper;

public class UpdateService extends IntentService {
    GetMacAdress getMacAdress;
    MainActivity mainActivity;
    App app = new App();
    long tocTime;

    public UpdateService() {
        super("UpdateService");
        mainActivity = new MainActivity();
        app = new App();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Intent serviceDownload = new Intent(this, DownloadService.class);

        final UrlHelper urlHelper = new UrlHelper(this);

        String urlUpdate = "https://kuriere.seqlab.eu/getAppVersion.php?vers=" + BuildConfig.VERSION_NAME + "&mac=" + getMacAdress.getMacAddr() + "&toc=" + Calendar.getInstance().getTime().getTime() / 1000;
        Log.i("UPDATE debug", urlUpdate);

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, urlUpdate,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getInt("error") == 0) {
                                if (obj.getInt("newflag") == 1) {
                                    String newUrlUpdate = obj.get("dlpath").toString();
                                    Uri uri = Uri.parse(obj.get("dlpath").toString());


                                    Toast.makeText(getApplicationContext(), "Ein Update wurde gefunden! Bitte warten...", Toast.LENGTH_LONG).show();
                                    urlHelper.updateUrl(newUrlUpdate);
                                    urlHelper.updateVersion(uri.getLastPathSegment());
                                    startService(serviceDownload);
                                    Log.i("UPDATE debug", "Service Download wird gestartet..." + "\n" + uri.getLastPathSegment());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Ihre App ist bereits auf dem neusten Stand", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Es ist ein Fehler aufgetreten!", Toast.LENGTH_LONG).show();

                                String newUrlUpdate = null;


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Es ist ein Fehler aufgetreten!", Toast.LENGTH_LONG).show();


                        Log.i("UPDATE debug", "Error Response");
                    }
                });

        VolleySingleton.getInstance(App.getContext()).addToRequestQueue(stringRequest);

    }
}

