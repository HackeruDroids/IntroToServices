package hackeru.edu.introtoservices;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        listenToReshetB(); //TODO: add remove Listener.
        setRecurringJob();
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


    private void setRecurringJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        int start = (int) java.util.concurrent.TimeUnit.HOURS.toSeconds(7); //7 hours in seconds
        int tolerance = (int) java.util.concurrent.TimeUnit.HOURS.toSeconds(1);   //1 hours in seconds
        Bundle extras = new Bundle();

        Job job = dispatcher.newJobBuilder().
                setService(MyJobService.class).
                setTag("MyRecurringUniqueTag").
                setExtras(extras).
                        // start between 20 seconds to 1 seconds from now recurring
                        setRecurring(true).
                        setReplaceCurrent(true).
                        setTrigger(Trigger.executionWindow(0, 20)).
                        setLifetime(Lifetime.FOREVER). //will survive reboot <uses permission>
//                        setConstraints(Constraint.DEVICE_CHARGING, Constraint.ON_UNMETERED_NETWORK).
                        build();
       // int res = dispatcher.schedule(job);
        dispatcher.mustSchedule(job);
    }

    //7-8 hours from now, one time job.
    private void setOneTimeJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        int start = (int) java.util.concurrent.TimeUnit.HOURS.toSeconds(7); //7 hours in seconds
        int end = (int) java.util.concurrent.TimeUnit.HOURS.toSeconds(8);   //8 hours in seconds

        Job job = dispatcher.newJobBuilder().
                setService(MyJobService.class).
                setTag("MyOtherUniqueTag").
                // start between 0 and 60 seconds from now
                        setTrigger(Trigger.executionWindow(start, end)).
                        setRecurring(false).
                        setLifetime(Lifetime.FOREVER).
                        setConstraints(Constraint.DEVICE_CHARGING, Constraint.ON_UNMETERED_NETWORK).
                        build();
        dispatcher.schedule(job);
    }

    //7 + 1
    private JobTrigger getTriggerForRecurringJob(int windowStart, int tolerance) {
        return Trigger.executionWindow(windowStart - tolerance, tolerance);
    }

    private void simpleJob() {
        //dispatcher-> job launcher
        //can also terminate jobs.

        //Scheduling a simple job
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        //Job job... JobDescription-> what, when, how, trigger?, Constraints
        Job job = dispatcher.newJobBuilder().
                setService(MyJobService.class).
                setTag("myUniqueTag").
                setTrigger(Trigger.NOW).
                build();

        dispatcher.schedule(job);
        //startService(new Intent(this, MyJobService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        //?Intent - > Explicit Intent
        Intent intent = new Intent(this/*Context*/, NotificationService.class);
        //startXXX
        startService(intent);

        push();
    }

    private void listenToReshetB() {

        //LocalBroadcastManager - an Object that can receive and broadcast intents
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this/*context*/);

        BroadcastReceiver listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String myData = intent.getStringExtra("myData");
                Toast.makeText(context, myData, Toast.LENGTH_SHORT).show();
            }
        };


        IntentFilter filter = new IntentFilter("ReshetB"); //which Channel?
        mgr.registerReceiver(listener, filter);
    }
}
