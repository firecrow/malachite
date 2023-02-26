package net.firesilver.sandbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.app.Notification;
import android.os.Parcelable;
import android.os.Parcel;

const val EXTRA_NOTIFICATION_INTENT = "net.firesilver.sandbox.EXTRA_NOTIFICATION"

class Receiver() : BroadcastReceiver() {
    override fun onReceive(context:Context, intent:Intent) {
        val packageName = intent.getStringExtra("package_name")
        Log.i("fcrow", "recieved "+packageName)
    }
}

class Service() : NotificationListenerService() {

    private lateinit var receiver: Receiver;
    override fun onCreate() {
        super.onCreate();
        Log.i("fcrow", "service thread starting")

        receiver = Receiver();
        val filter = IntentFilter();
        filter.addAction(EXTRA_NOTIFICATION_INTENT)
        this.registerReceiver(receiver, filter);
    }

    override fun onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i("fcrow","posted "+sbn.getPackageName()+": "+sbn.getNotification().tickerText);

        val intent = Intent(EXTRA_NOTIFICATION_INTENT)
        intent.putExtra("package_name", sbn.getPackageName())
        intent.putExtra("type", "received");
        //intent.putExtra("parcel", intent);

        sendBroadcast(intent);
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i("fcrow","removed "+sbn.getPackageName()+": "+sbn.getNotification().tickerText);

        val intent = Intent(EXTRA_NOTIFICATION_INTENT)
        intent.putExtra("package_name", sbn.getPackageName())
        intent.putExtra("type", "removed");
        //intent.putExtra("parcel", intent);

        sendBroadcast(intent);
    }
}