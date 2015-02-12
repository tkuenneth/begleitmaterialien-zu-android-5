package com.thomaskuenneth.missedcalls;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

public class MissedCallsConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Welche Farbe?
                int color = MissedCallsWidget.COLOR_SEMI_TRANSPARENT;
                switch (v.getId()) {
                    case R.id.red:
                        color = Color.RED;
                        break;
                    case R.id.green:
                        color = Color.GREEN;
                        break;
                }
                // Id des Widgets ermitteln
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int mAppWidgetId = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                    if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        // ausgewählte Farbe speichern
                        SharedPreferences prefs = getSharedPreferences(MissedCallsWidget.MCW, MODE_PRIVATE);
                        SharedPreferences.Editor e = prefs.edit();
                        e.putInt(MissedCallsWidget.BACKGROUND_COLOR + Integer.toString(mAppWidgetId), color);
                        e.apply();
                        // Farbe setzen
                        RemoteViews views = new RemoteViews(getPackageName(),
                                R.layout.missedcallswidget_layout);
                        views.setInt(R.id.background, "setBackgroundColor", color);
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MissedCallsConfigActivity.this);
                        appWidgetManager.partiallyUpdateAppWidget(mAppWidgetId, views);
                        // Änderungen übernehmen
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        setResult(RESULT_OK, resultValue);
                    }
                }
                // Activity beenden
                finish();
            }
        };
        findViewById(R.id.transparent).setOnClickListener(l);
        findViewById(R.id.red).setOnClickListener(l);
        findViewById(R.id.green).setOnClickListener(l);
        // standardmäßig die Einstellungen nicht übernehmen
        setResult(RESULT_CANCELED);
    }
}
