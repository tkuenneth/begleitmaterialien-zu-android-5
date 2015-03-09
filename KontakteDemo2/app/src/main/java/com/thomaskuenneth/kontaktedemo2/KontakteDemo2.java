package com.thomaskuenneth.kontaktedemo2;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KontakteDemo2 extends Activity {

    private static final String TAG = KontakteDemo2.class.getSimpleName();

    /**
     * Datum im Format jjjjmmtt, also 19700829
     */
    private static final SimpleDateFormat FORMAT_YYYYMMDD
            = new SimpleDateFormat("yyyyMMdd");

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
                infosAuslesen(contentResolver, contactId);
            }
            mainQueryCursor.close();
        }
    }

    private void infosAuslesen(ContentResolver contentResolver, String contactId) {
        String[] dataQueryProjection = new String[]{
                ContactsContract.CommonDataKinds.Event.TYPE,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.LABEL};
        String dataQuerySelection = ContactsContract.Data.CONTACT_ID
                + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] dataQuerySelectionArgs = new String[]{contactId,
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        Cursor dataQueryCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI, dataQueryProjection,
                dataQuerySelection, dataQuerySelectionArgs, null);
        if (dataQueryCursor != null) {
            while (dataQueryCursor.moveToNext()) {
                int type = dataQueryCursor.getInt(0);
                String label = dataQueryCursor.getString(2);
                if (ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY == type) {
                    String stringBirthday = dataQueryCursor.getString(1);
                    Log.d(TAG, "     birthday: " + stringBirthday);
                } else {
                    String stringAnniversary = dataQueryCursor.getString(1);
                    Log.d(TAG, "     event: " + stringAnniversary + " (type="
                            + type + ", label=" + label + ")");
                    if (ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY == type) {
                        Log.d(TAG, "     TYPE_ANNIVERSARY");
                    } else if (ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM == type) {
                        Log.d(TAG, "     TYPE_CUSTOM");
                    } else {
                        Log.d(TAG, "     TYPE_OTHER");
                    }
                }
            }
            dataQueryCursor.close();
        }
    }

    public static Date getDateFromString1(String string) {
        Date result = null;
        if (string != null) {
            Pattern p = Pattern.compile(
                    "(\\d\\d\\d\\d).*(\\d\\d).*(\\d\\d)",
                    Pattern.DOTALL);
            Matcher m = p.matcher(string.subSequence(0,
                    string.length()));
            if (m.matches()) {
                String date = m.group(1) + m.group(2) + m.group(3);
                try {
                    result = FORMAT_YYYYMMDD.parse(date);
                } catch (Throwable tr) {
                    Log.e(TAG, "getDateFromString1()", tr);
                }
            }
        }
        return result;
    }
}