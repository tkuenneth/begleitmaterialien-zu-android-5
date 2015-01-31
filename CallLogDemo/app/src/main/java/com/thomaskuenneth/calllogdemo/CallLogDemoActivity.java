package com.thomaskuenneth.calllogdemo;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CallLogDemoActivity extends ListActivity {

    private ContentObserver contentObserver;
    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null,
                new String[]{CallLog.Calls.NUMBER}, new int[]{android.R.id.text1}, 0);
        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndex(CallLog.Calls.NUMBER)) {
                    String number = cursor.getString(columnIndex);
                    int isNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW));
                    if (isNew != 0) {
                        number += " (neu)";
                    }
                    ((TextView) view).setText(number);
                    return true;
                }
                return false;
            }
        });
        setListAdapter(cursorAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) cursorAdapter.getItem(position);
                if (c != null) {
                    long callLogId = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
                    updateCallLogData(callLogId);
                    updateAdapter();
                }
            }
        });

        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                updateAdapter();
            }
        };
        getContentResolver().registerContentObserver(
                CallLog.Calls.CONTENT_URI,
                false, contentObserver);
        updateAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
    }

    private void updateAdapter() {
        final ContentResolver cr = getContentResolver();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final Cursor c = getMissedCalls(cr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cursorAdapter.changeCursor(c);
                    }
                });
            }
        });
        t.start();
    }

    private Cursor getMissedCalls(ContentResolver r) {
        String[] projection = {CallLog.Calls.NUMBER, CallLog.Calls.DATE,
                CallLog.Calls.NEW, CallLog.Calls._ID};
        String selection = CallLog.Calls.TYPE + " = ?";
        String[] selectionArgs = {
                Integer.toString(CallLog.Calls.MISSED_TYPE)};
        return r.query(CallLog.Calls.CONTENT_URI,
                projection, selection,
                selectionArgs, null);
    }

    private void updateCallLogData(long id) {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NEW, 0);
        String where = CallLog.Calls._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        getContentResolver().
                update(CallLog.Calls.CONTENT_URI,
                        values, where, selectionArgs);
    }
}
