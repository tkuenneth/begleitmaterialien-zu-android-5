package com.thomaskuenneth.kontaktedemo1;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class KontakteDemo1 extends Activity {

    private static final String TAG = KontakteDemo1.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver contentResolver = getContentResolver();
        // IDs und Namen aller sichtbaren Kontakte ermitteln
        String[] mainQueryProjection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        String mainQuerySelection = ContactsContract.Contacts.IN_VISIBLE_GROUP
                + " = ?";
        String[] mainQuerySelectionArgs = new String[]{"1"};
        Cursor mainQueryCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, mainQueryProjection,
                mainQuerySelection, mainQuerySelectionArgs, null);
        // Trefferliste abarbeiten...
        if (mainQueryCursor != null) {
            while (mainQueryCursor.moveToNext()) {
                String contactId = mainQueryCursor.getString(0);
                String displayName = mainQueryCursor.getString(1);
                Log.d(TAG, "===> " + displayName + " (" + contactId + ")");
            }
            mainQueryCursor.close();
        }
    }
}