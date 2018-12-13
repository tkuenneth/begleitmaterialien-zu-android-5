package com.thomaskuenneth.missedcalls;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.jetbrains.annotations.NotNull;

public class MissedCallsConfigActivity extends Activity {

    private static final int RQ_PERMISSIONS = 0x1234;

    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        findViewById(R.id.transparent).setOnClickListener(v -> updateWidget((MissedCallsWidget.COLOR_SEMI_TRANSPARENT)));
        findViewById(R.id.red).setOnClickListener(v -> updateWidget((Color.RED)));
        findViewById(R.id.green).setOnClickListener(v -> updateWidget((Color.GREEN)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        resultIntent = new Intent();
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetIdFromIntent(getIntent()));
        setResult(RESULT_CANCELED, resultIntent);
        if (!MissedCallsWidget.canReadCallLog(this)) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, RQ_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if ((grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            MissedCallsWidget.sendBroadcast(this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateWidget(int color) {
        int mAppWidgetId = getAppWidgetIdFromIntent(resultIntent);
        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            SharedPreferences prefs = getSharedPreferences(MissedCallsWidget.MCW, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            e.putInt(MissedCallsWidget.getColorKey(mAppWidgetId), color);
            e.apply();
            RemoteViews views = MissedCallsWidget.getRemoteViews(this, prefs, mAppWidgetId);
            AppWidgetManager m = AppWidgetManager.getInstance(this);
            m.updateAppWidget(mAppWidgetId, views);
            resultIntent.putExtra(MissedCallsWidget.getColorKey(mAppWidgetId), color);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    private int getAppWidgetIdFromIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            return extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return AppWidgetManager.INVALID_APPWIDGET_ID;
    }
}
