package net.firesilver.sandbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.app.Notification;
import android.app.Person
import android.os.Bundle
import android.os.Parcelable;
import android.os.Parcel;

const val EXTRA_NOTIFICATION_INTENT = "net.firesilver.sandbox.EXTRA_NOTIFICATION"

const val BUNDLE_TYPE_STRING = 1
const val BUNDLE_TYPE_TEXTLINES = 2
const val BUNDLE_TYPE_IMAGE = 3
const val BUNDLE_TYPE_PEOPLE_LIST = 4
const val BUNDLE_TYPE_PERSON = 5
const val BUNDLE_TYPE_INT = 6
const val BUNDLE_TYPE_BOOL = 7
const val BUNDLE_TYPE_MESSAGES = 8

val fieldWhitelist = mutableMapOf(
    "android.title" to BUNDLE_TYPE_STRING,
    "android.hiddenConversationTitle" to BUNDLE_TYPE_STRING,
    "android.subText" to BUNDLE_TYPE_STRING,
    "android.text" to BUNDLE_TYPE_STRING,
    "android.showWhen" to BUNDLE_TYPE_BOOL,
    "android.textLines" to BUNDLE_TYPE_TEXTLINES,
    "android.people.list" to BUNDLE_TYPE_STRING,
    "android.messages" to BUNDLE_TYPE_MESSAGES,
    "android.infoText" to BUNDLE_TYPE_STRING,
    "android.largeIcon" to BUNDLE_TYPE_IMAGE,
    "android.summaryText" to BUNDLE_TYPE_STRING,
    "android.people.list" to BUNDLE_TYPE_PEOPLE_LIST,
    "android.messagingUser" to BUNDLE_TYPE_PERSON,
    Notification.EXTRA_INFO_TEXT to BUNDLE_TYPE_STRING,
    Notification.EXTRA_TITLE to BUNDLE_TYPE_STRING,
    Notification.EXTRA_TEXT to BUNDLE_TYPE_STRING,
)

class Receiver() : BroadcastReceiver() {
    override fun onReceive(context:Context, intent:Intent) {
        val packageName = intent.getStringExtra("package_name")
        val bundle:Bundle? = intent.extras?.getParcelable("notification")
        Log.i("fcrow", "recieved "+packageName)
        bundle?.let {
            for(k in bundle.keySet()) {
                val bundleType = fieldWhitelist[k]
                if(bundleType == BUNDLE_TYPE_STRING) {
                    Log.i(
                        "fcrow",
                        "received notification -> " + k + ": " + bundle.getString(k).toString()
                    )
                } else if(bundleType == BUNDLE_TYPE_INT) {
                    Log.i(
                        "fcrow",
                        "received notification -> " + k + ": " + bundle.getInt(k).toString()
                    )
                } else if(bundleType == BUNDLE_TYPE_BOOL) {
                    Log.i(
                        "fcrow",
                        "received notification -> " + k + ": " + bundle.getBoolean(k).toString()
                    )
                } else if(bundleType == BUNDLE_TYPE_TEXTLINES) {
                    var lines = bundle.getCharSequenceArray(k)
                    lines?.let {
                        for (l in lines) {
                            Log.i(
                                "fcrow",
                                "received notification -> " + k + ": " + l?.toString()
                            )
                        }
                    }
                } else if(bundleType == BUNDLE_TYPE_PEOPLE_LIST) {
                    val people: ArrayList<Parcelable>? = bundle.getParcelableArrayList(k)
                    Log.i("fcrow", "received notification found how many people?: "+people?.size.toString())
                    people?.let {
                        for (p in people) {
                            val person: Person? = p as Person
                            Log.i(
                                "fcrow",
                                "received notification people -> " + k + ": "+ p.toString() + " " + p?.name + " " +p?.key
                            )
                        }
                    }
                } else if(bundleType == BUNDLE_TYPE_PERSON) {
                    val person: Person? = bundle.getParcelable(k)
                    Log.i(
                        "fcrow",
                        "received notification  -> person " + k + ": " + person?.name + " " +person?.key
                    )
                }else{
                    Log.i(
                        "fcrow",
                        "received notification -> " + k
                    )
                }
            }
        }
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
        Log.i("fcrow","posted "+sbn.packageName+": "+sbn.notification.tickerText);

        val intent = Intent(EXTRA_NOTIFICATION_INTENT)
        intent.putExtra("package_name", sbn.packageName)
        intent.putExtra("type", "received");
        val extras = sbn.notification.extras
        val bundle = Bundle();
        for(k in extras.keySet()) {
            Log.i("fcrow", "found " + k)
            val fieldType = fieldWhitelist[k]
            if (fieldType == BUNDLE_TYPE_STRING) {
                bundle.putString(k, extras.getString(k))
            } else if (fieldType == BUNDLE_TYPE_TEXTLINES) {
                var sequence: Array<CharSequence>? = extras.getCharSequenceArray(k)
                sequence?.let {
                    bundle.putCharSequenceArray(k, sequence)
                }
            } else if (fieldType == BUNDLE_TYPE_IMAGE) {
                bundle.putParcelable(k, extras.getParcelable(k))
            } else if (fieldType == BUNDLE_TYPE_INT) {
                bundle.putInt(k, extras.getInt(k))
            } else if (fieldType == BUNDLE_TYPE_BOOL) {
                bundle.putBoolean(k, extras.getBoolean(k))
            } else if (fieldType == BUNDLE_TYPE_PEOPLE_LIST) {
                bundle.putParcelableArrayList(k, extras.getParcelableArrayList(k))
            } else if (fieldType == BUNDLE_TYPE_PERSON) {
                bundle.putParcelable(k, extras.getParcelable(k))
            }
        }
        intent.putExtra("notification", bundle);

        sendBroadcast(intent);
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i("fcrow","removed "+sbn.getPackageName()+": "+sbn.getNotification().tickerText);

        val intent = Intent(EXTRA_NOTIFICATION_INTENT)
        intent.putExtra("package_name", sbn.packageName)
        intent.putExtra("type", "removed");

        sendBroadcast(intent);
    }
}