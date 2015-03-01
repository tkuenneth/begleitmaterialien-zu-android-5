package com.thomaskuenneth.userdictionarydemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.UserDictionary.Words;
import android.util.Log;

import java.util.Locale;

public class UserDictionaryDemo extends Activity {

    private static final String TAG = UserDictionaryDemo.class.getSimpleName();
    private static final String WORT = "Künneth";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver cr = getContentResolver();
        Log.d(TAG, "Erster Aufruf");
        listEntries(cr);
        // Eintrag hinzufügen
        ContentValues values = new ContentValues();
        values.put(Words.APP_ID, getApplication().getApplicationInfo().uid);
        values.put(Words.WORD, WORT);
        values.put(Words.FREQUENCY, 250);
        values.put(Words.LOCALE, Locale.GERMAN.toString());
        cr.insert(Words.CONTENT_URI, values);
        Log.d(TAG, "Zweiter Aufruf");
        listEntries(cr);
        // Wort löschen
        cr.delete(Words.CONTENT_URI, Words.WORD + " is ?",
                new String[]{WORT});
        Log.d(TAG, "Dritter Aufruf");
        listEntries(cr);
    }

    private void listEntries(ContentResolver cr) {
        String[] projection = {Words.WORD, Words.FREQUENCY, Words.APP_ID,
                Words.LOCALE, BaseColumns._ID};
        Cursor c = cr.query(Words.CONTENT_URI, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                String word = c.getString(0);
                int freq = c.getInt(1);
                int apid = c.getInt(2);
                String locale = c.getString(3);
                long _id = c.getLong(4);
                Log.d(TAG, word + " (freq=" + freq + ", apid=" + apid + ", locale="
                        + locale + ", _id=" + _id + ")");
            }
            c.close();
        }
    }
}