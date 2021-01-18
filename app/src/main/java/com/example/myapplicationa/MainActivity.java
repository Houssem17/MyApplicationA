package com.example.myapplicationa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FIRST OF ALL
        // START SERVICE AS STICKY - BY EXPLICIT INTENT
        // to prevent being started by the system (without the sticky flag)
        Intent intent = new Intent(getApplicationContext(), NLService.class);




        //starting service


        startService(intent);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(i);
        //setting button click
        findViewById(R.id.btn_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ask service (async) for its status (manually after the main activity was paused or closed and missed some NLService's broadcasts)
                // the background service is sticky and the counters(added/removed) are not reset.

                //Creating an intent for sending to service
                //Intent intent = new Intent(getApplicationContext(), MyService.class);
                Intent intent = new Intent(getApplicationContext(), NLService.class);
                intent.putExtra("command", "get_status");
                //starting service
                startService(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // register broadcast receiver for the intent MyTaskStatus
        LocalBroadcastManager.getInstance(this).registerReceiver(MyReceiver, new IntentFilter(NLService.ACTION_STATUS_BROADCAST));


    }


    //Defining broadcast receiver
    private BroadcastReceiver MyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("MainActivity", "Broadcast Recieved: "+intent.getStringExtra("serviceMessage"));
            String message = intent.getStringExtra("serviceMessage");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };




    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(MyReceiver);
    }

}