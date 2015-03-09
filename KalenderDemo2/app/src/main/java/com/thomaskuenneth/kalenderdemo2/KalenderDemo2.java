package com.thomaskuenneth.kalenderdemo2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;

public class KalenderDemo2 extends Activity {

    private static final String TAG = KalenderDemo2.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor c = getContentResolver().query(Events.CONTENT_URI, null, null,
                null, null);
        if (c != null) {
            int indexId = c.getColumnIndex(Events._ID);
            int indexTitle = c.getColumnIndex(Events.TITLE);
            while (c.moveToNext()) {
                Log.d(TAG, "_ID: " + c.getString(indexId));
                Log.d(TAG, "TITLE: " + c.getString(indexTitle));
            }
            c.close();
        }
    }
}