package com.thomaskuenneth.missedcalls;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.widget.RemoteViews;

public class MissedCallsWidget extends AppWidgetProvider {

    public static final int COLOR_SEMI_TRANSPARENT = 0x80000000;
    public static final String MCW = "MissedCallsWidget";

    private static final String TAG = MissedCallsWidget.class.getSimpleName();
    private static final String BC = "BC_";

    private ContentObserver contentObserver = null;
    private int lastMissedCalls = -1;

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled()");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled()");
        unregisterContentObserver(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        registerContentObserver(context);
        SharedPreferences prefs = context.getSharedPreferences(MCW, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            final RemoteViews views = getRemoteViews(context, prefs, id);
            appWidgetManager.updateAppWidget(id, views);
        }
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(MCW, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        for (int i = 0; i < oldWidgetIds.length; i++) {
            String oldWidgetKey = getColorKey(oldWidgetIds[i]);
            int color = prefs.getInt(oldWidgetKey, COLOR_SEMI_TRANSPARENT);
            e.remove(oldWidgetKey);
            e.putInt(getColorKey(newWidgetIds[i]), color);
        }
        e.apply();
    }

    public static RemoteViews getRemoteViews(Context context, SharedPreferences prefs,
                                             int id) {
        int color = getColor(prefs, id);
        boolean canRead = canReadCallLog(context);
        final RemoteViews views = new RemoteViews(context.getPackageName(),
                canRead ? R.layout.missedcallswidget_layout : R.layout.widget_initial_layout);
        views.setInt(R.id.background, "setBackgroundColor", color);
        // Konfigurations-Activity
        Intent configIntent = new Intent(context, MissedCallsConfigActivity.class);
        configIntent.setData(Uri.withAppendedPath(Uri.parse("abc://widget/id/"), String.valueOf(id)));
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0,
                configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Call Log
        Intent callLogIntent = new Intent(Intent.ACTION_VIEW);
        callLogIntent.setDataAndType(CallLog.Calls.CONTENT_URI, CallLog.Calls.CONTENT_TYPE);
        PendingIntent callLogPendingIntent = PendingIntent.getActivity(context, 0,
                callLogIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (canRead) {
            views.setOnClickPendingIntent(R.id.config, configPendingIntent);
            views.setOnClickPendingIntent(R.id.background, callLogPendingIntent);
            views.setTextViewText(R.id.missedcallswidget_count,
                    Integer.toString(getMissedCalls(context)));
        } else {
            views.setTextViewText(R.id.tv, context.getString(R.string.missing_permission));
            views.setOnClickPendingIntent(R.id.background, configPendingIntent);
        }
        return views;
    }

    public static String getColorKey(int id) {
        return BC + Integer.toString(id);
    }

    public static boolean canReadCallLog(Context context) {
        return context.checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void sendBroadcast(Context context) {
        Intent intent = new Intent(context, MissedCallsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(intent.getComponent());
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void registerContentObserver(Context context) {
        if (canReadCallLog(context)) {
            if (contentObserver == null) {
                contentObserver = new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        int missedCalls = getMissedCalls(context);
                        Log.d(TAG, "onChange() - " + missedCalls + " entgangene Anrufe");
                        if (missedCalls != lastMissedCalls) {
                            lastMissedCalls = missedCalls;
                            sendBroadcast(context);
                        }
                    }
                };
                context.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
                        false, contentObserver);
            }
        }
    }

    private void unregisterContentObserver(Context context) {
        if (canReadCallLog(context)) {
            if (contentObserver != null) {
                context.getContentResolver().unregisterContentObserver(contentObserver);
            }
        }
        contentObserver = null;
        lastMissedCalls = -1;
    }

    private static int getColor(SharedPreferences prefs, int id) {
        return prefs.getInt(getColorKey(id), COLOR_SEMI_TRANSPARENT);
    }

    private static int getMissedCalls(Context context) {
        int missedCalls = -1;
        if (canReadCallLog(context)) {
            String[] projection = {CallLog.Calls._ID};
            String selection = CallLog.Calls.TYPE + " = ?";
            String[] selectionArgs = {Integer.toString(CallLog.Calls.MISSED_TYPE)};
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
}
