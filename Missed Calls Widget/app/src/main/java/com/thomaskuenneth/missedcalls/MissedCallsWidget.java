package com.thomaskuenneth.missedcalls;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CallLog;
import android.util.Log;
import android.widget.RemoteViews;

public class MissedCallsWidget extends AppWidgetProvider {

    public static final String BACKGROUND_COLOR = "BC_";
    public static final int COLOR_SEMI_TRANSPARENT = 0x80000000;
    public static final String MCW = "MissedCallsWidget";

    private static final String TAG = MissedCallsWidget.class.getSimpleName();

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled()");
        super.onEnabled(context);
        Intent service = new Intent(context, MissedCallsService.class);
        context.startService(service);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled()");
        super.onDisabled(context);
        Intent service = new Intent(context, MissedCallsService.class);
        context.stopService(service);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        super.onReceive(context, intent);
        if (MissedCallsService.ACTION_MISSED_CALL
                .equals(intent.getAction())) {
            int missedCalls = intent.getIntExtra(
                    MissedCallsService.EXTRA_MISSED_CALL, -1);
            if (missedCalls != -1) {
                AppWidgetManager m = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = m
                        .getAppWidgetIds(new ComponentName(context,
                                getClass()));
                updateWidgets(context, m, appWidgetIds, missedCalls);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        updateWidgets(context, appWidgetManager, appWidgetIds,
                MissedCallsService.getMissedCalls(context));
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        SharedPreferences prefs = context.getSharedPreferences(MCW, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        for (int i = 0; i < oldWidgetIds.length; i++) {
            int color = prefs.getInt(BACKGROUND_COLOR + Integer.toString(oldWidgetIds[i]), COLOR_SEMI_TRANSPARENT);
            e.putInt(BACKGROUND_COLOR + Integer.toString(newWidgetIds[i]), color);
        }
        e.apply();
    }

    private void updateWidgets(Context context,
                               AppWidgetManager appWidgetManager, int[] appWidgetIds,
                               int missedCalls) {
        SharedPreferences prefs = context.getSharedPreferences(MCW, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            int color = prefs.getInt(BACKGROUND_COLOR + Integer.toString(id), COLOR_SEMI_TRANSPARENT);
            // Layoutdatei entfalten
            final RemoteViews updateViews = new RemoteViews(
                    context.getPackageName(), R.layout.missedcallswidget_layout);
            // Farbe setzen
            updateViews.setInt(R.id.background, "setBackgroundColor", color);
            // Zahl entgangener Anrufe ausgeben
            updateViews.setTextViewText(R.id.missedcallswidget_count,
                    Integer.toString(missedCalls));
            // auf Antippen reagieren
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    CallLog.Calls.CONTENT_URI);
            intent.setType(CallLog.Calls.CONTENT_TYPE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            updateViews.setOnClickPendingIntent(R.id.widget_id, pendingIntent);
            appWidgetManager.updateAppWidget(id, updateViews);
        }
    }
}
