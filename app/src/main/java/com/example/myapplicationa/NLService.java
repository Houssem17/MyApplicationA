package com.example.myapplicationa;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

//public class NLService extends NotificationListenerService {
/*
    /*
        These are the package names of the apps. for which we want to
        listen the notifications

    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
  /*  public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int WHATSAPP_CODE = 2;
        public static final int INSTAGRAM_CODE = 3;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        //String subtext = "";

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE)
        {



                final Intent intent=new Intent();
                intent.setAction("com.example.myapplicationa.notificationlistenerexample");
                intent.putExtra("Notification Code", notificationCode);
                intent.putExtra("data", "Some data");
                intent.putExtra("package", pack);
                intent.putExtra("title", title);
                //intent.putExtra("text", subtext);
                intent.putExtra("id", sbn.getId());


                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.setComponent(new ComponentName("com.example.myapplicationb","com.example.myapplicationb.MyBroadcastReceiver"));
                sendBroadcast(intent);



    }}

 /*   @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (int i = 0; i < activeNotifications.length; i++) {
                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
                        Intent intent = new  Intent("com.example.myapplicationa.notificationlistenerexample");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }
/*

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}
*/
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NLService extends NotificationListenerService {

    public static final String ACTION_STATUS_BROADCAST = "com.example.myapplicationa.NLService_Status";

    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;

    /**
     * The number of notifications added (since the service started)
     */
    private int nAdded=0;
    /**
     * The number of notifications removed (since the service started)
     */
    private int nRemoved=0;
    int temp = 5;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //retrieving data from the received intent
        if(intent.hasExtra("command")) {
            Log.i("NLService", "Started for command '"+intent.getStringExtra("command"));
            broadcastStatus();
        } else if(intent.hasExtra("id")) {
            int id = intent.getIntExtra("id", 0);
            String message = intent.getStringExtra("msg");
            Log.i("NLService", "Requested to start explicitly - id : " + id + " message : " + message);
        }
        super.onStartCommand(intent, flags, startId);

        // NOTE: We return STICKY to prevent the automatic service termination
        return START_STICKY;
    }

    private void broadcastStatus() {
        Log.i("NLService", "Broadcasting status added("+nAdded+")/removed("+nRemoved+")");
        Intent i1 = new  Intent(ACTION_STATUS_BROADCAST);
        i1.putExtra("serviceMessage","Added: "+nAdded+" | Removed: "+nRemoved);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i1);
        // sendBroadcast(i1);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NLService", "NLService created!");
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.myapplicationa.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
        Log.i("NLService", "NLService created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
        Log.i("NLService", "NLService destroyed!");
    }

    /* > API 21
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.w("NLService", "Notification listener DISCONNECTED from the notification service! Scheduling a reconnect...");
        // requestRebind(new ComponentName(this.getPackageName(), this.getClass().getCanonicalName()));
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.w("NLService", "Notification listener connected with the notification service!");
    }
    */

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();



        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent("com.example.myapplicationa.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("package", pack);
        i.putExtra("id", sbn.getId());
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        i.setComponent(new ComponentName("com.example.myapplicationb","com.example.myapplicationb.MyBroadcastReceiver"));
        sendBroadcast(i);

        nAdded++;

        if (nAdded == temp) {
            System.out.println("reached" + temp);
            temp = temp + 5;
            System.out.println(temp);
            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
            ncomp.setContentTitle("Notification");
            ncomp.setContentText("Notification Listener Service Example");
            ncomp.setTicker("Notification Listener Service Example");
            ncomp.setSmallIcon(R.mipmap.ic_launcher);
            ncomp.setAutoCancel(true);
            nManager.notify((int)System.currentTimeMillis(),ncomp.build());
        }
        broadcastStatus();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent("com.example.myapplicationa.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);

        nRemoved++;
        broadcastStatus();
    }

    class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("com.example.myapplicationa.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("com.example.myapplicationa.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("com.example.myapplicationa.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }

}