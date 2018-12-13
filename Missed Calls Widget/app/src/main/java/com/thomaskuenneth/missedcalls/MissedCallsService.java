package com.thomaskuenneth.missedcalls;

import android.Manifest;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;

public class MissedCallsService extends Service {

    private static final String TAG = MissedCallsService.class.getSimpleName();

    private ContentObserver contentObserver;
    private int lastMissedCalls;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        lastMissedCalls = -1;
        contentObserver = null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (canReadCallLog(this)) {
            if (contentObserver != null) {
                getContentResolver().unregisterContentObserver(contentObserver);
            }
        }
        contentObserver = null;
        lastMissedCalls = -1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (canReadCallLog(this)) {
            if (contentObserver == null) {
                contentObserver = new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        int missedCalls = getMissedCalls(MissedCallsService.this);
                        Log.d(TAG, "onChange() - " + missedCalls + " entgangene Anrufe");
                        if (missedCalls != lastMissedCalls) {
                            lastMissedCalls = missedCalls;
                            // Intent erstellen und senden
                            Intent i = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                            sendBroadcast(i);
                        }
                    }
                };
                getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
                        false, contentObserver);
            }
        }
        return START_STICKY;
    }

    public static int getMissedCalls(Context context) {
        int missedCalls = -1;
        if (canReadCallLog(context)) {
            String[] projection = {Calls._ID};
            String selection = Calls.TYPE + " = ?";
            String[] selectionArgs = {Integer.toString(Calls.MISSED_TYPE)};
            Cursor c = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, projection, selection,
                    selectionArgs, null);
            if (c != null) {
                missedCalls = c.getCount();
                c.close();
            }
            Log.d(TAG, "getMissedCalls() - " + missedCalls + " entgangene Anrufe");
        }
        return missedCalls;
    }

    public static boolean canReadCallLog(Context context) {
        return context.checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void startService(Context context) {
        if (MissedCallsService.canReadCallLog(context)) {
            Intent intent = new Intent(context, MissedCallsService.class);
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, MissedCallsService.class);
        context.stopService(intent);
    }
}
