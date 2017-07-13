package hackeru.edu.introtoservices;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.Date;

/**
 * Unit of work that we want to execute in the background.
 */
//service
public class MyJobService extends JobService {
    public static final String TAG = "Hackeru";

    //start the job
    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.d(TAG, "onStartJob: Working");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //do your job
                Log.d(TAG, "run: Faking the work");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                push();
                jobFinished(job, true);
            }
        });
        t.start();
        return true; //work still in progress?
    }

    //stop the job
    @Override
    public boolean onStopJob(JobParameters job) {
        return false; //"Should this job be retried?"
    }

    private void push() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("time", new Date().toString());

        int rcPending = 2;
        PendingIntent pi = PendingIntent.getActivity(this/*context*/,rcPending , intent /*intent*/, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pi2 = PendingIntent.getActivity(this/*context*/,rcPending , intent /*intent*/, PendingIntent.FLAG_UPDATE_CURRENT);

        //service->Context = this
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this/*context*/);
        //title, text, smallIcon
        builder.setContentTitle("A new Push (Title)").
                setContentText("This is the text").
                setContentIntent(pi).
                setAutoCancel(true).
                setPriority(Notification.PRIORITY_HIGH).
                setDefaults(Notification.DEFAULT_ALL). //sound, vibrate, lights
                setSmallIcon(R.drawable.ic_notification);

        Notification notification = builder.build();
        //LayoutInflater.from(this);
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this /*context*/);

        mgr.notify(1, notification);
        //mgr.cancel

    }

}
