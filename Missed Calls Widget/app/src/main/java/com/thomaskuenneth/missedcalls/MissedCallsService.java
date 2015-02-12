package com.thomaskuenneth.missedcalls;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.util.Log;

public class MissedCallsService extends Service {

    public static final String ACTION_MISSED_CALL = "com.thomaskuenneth.missedcalls.MISSED_CALLS";
    public static final String EXTRA_MISSED_CALL = "MISSED_CALLS";

    private static final String TAG = MissedCallsService.class.getSimpleName();

    private ContentObserver contentObserver;
    private int lastMissedCalls;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        // Service wird nicht mit bindService() gebunden
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        lastMissedCalls = -1;
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                int missedCalls = getMissedCalls(MissedCallsService.this);
                Log.d(TAG, "onChange() - " + missedCalls + " entgangene Anrufe");
                if (missedCalls != lastMissedCalls) {
                    lastMissedCalls = missedCalls;
                    // Intent erstellen und senden
                    Intent i = new Intent(ACTION_MISSED_CALL);
                    i.putExtra(EXTRA_MISSED_CALL, missedCalls);
                    sendBroadcast(i);
                }
            }
        };
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
                false, contentObserver);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
        lastMissedCalls = -1;
    }

    public static int getMissedCalls(Context context) {
        int missedCalls = -1;
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
        return missedCalls;
    }
}
