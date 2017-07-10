package hackeru.edu.introtoservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    }

    private void listenToReshetB(){
        //LocalBroadcastManager
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
