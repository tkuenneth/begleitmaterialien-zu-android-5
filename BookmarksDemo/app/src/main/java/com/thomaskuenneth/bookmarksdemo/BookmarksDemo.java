package com.thomaskuenneth.bookmarksdemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;

public class BookmarksDemo extends Activity {
    private static final String TAG = BookmarksDemo.class.getSimpleName();
    private static final String RHEINWERK_VERLAG = "Rheinwerk Verlag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Content Resolver ermitteln
        ContentResolver cr = getContentResolver();
        // ein Lesezeichen hinzufügen
        ContentValues values = new ContentValues();
        values.put(Browser.BookmarkColumns.TITLE, RHEINWERK_VERLAG);
        values.put(Browser.BookmarkColumns.BOOKMARK, 1);
        values.put(Browser.BookmarkColumns.URL,
                "https://www.rheinwerk-verlag.de/");
        values.put(Browser.BookmarkColumns.VISITS, 0);
        cr.insert(Browser.BOOKMARKS_URI, values);
        // Liste ausgeben
        String[] projection = {Browser.BookmarkColumns.TITLE,
                Browser.BookmarkColumns.BOOKMARK, Browser.BookmarkColumns.URL,
                Browser.BookmarkColumns.VISITS};
        Cursor c = cr
                .query(Browser.BOOKMARKS_URI, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                Log.d(TAG, "Titel: " + c.getString(0)
                        + (c.getInt(1) == 0 ? " (Historyeintrag)" : ""));
                Log.d(TAG, "URL: " + c.getString(2) + " (" + c.getInt(3)
                        + " mal besucht)");
            }
            // Ressourcen freigeben
            c.close();
        }
        // Lesezeichen löschen
        cr.delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns.TITLE
                + " is ?", new String[]{RHEINWERK_VERLAG});
    }
}
