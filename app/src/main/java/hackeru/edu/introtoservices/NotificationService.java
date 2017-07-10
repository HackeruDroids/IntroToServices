package hackeru.edu.introtoservices;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//started Services
//Bound Services


//IntentService extends Service
//A Simple service runs on the ui thread

//An Intent Service runs on a secondary thread.
public class NotificationService extends IntentService {
    public static final String TAG = "NotificationService";

    public NotificationService() {
        //The name is used for debugging purposes
        //super(NotificationService.class.getName());
        //super("NotificationService");
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //take the mission here: a starting point for the service
        //here we receive the intent that started the service
        /*
        Intent - > Explicit Intent
        Intent intent = new Intent(this/*Context,NotificationService.class)
        startXXX
        startService(intent);
        */

        //A Service must not provide UI:
        //a Service may send push Notification
        BufferedReader reader = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL("https://github.com/JakeWharton/butterknife");
            con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            Log.d(TAG, "onHandleIntent: " + result);
            //shout the result:
            shout(result.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {//3.0 alpha-beta-> JAVA 8
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void shout(String s) {
        //1) LocalBroadcastManager
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this/*Service is a context*/);

        //2) sendBroadcast
        Intent intent = new Intent("ReshetB"/*Action*/);
        intent.putExtra("myData", s);

        mgr.sendBroadcast(intent);
    }
}
