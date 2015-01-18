package com.thomaskuenneth.minicontacts;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MiniContacts extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] PROJECTION =
            new String[]{ContactsContract.Data._ID,
                    ContactsContract.Data.LOOKUP_KEY,
                    ContactsContract.Data.DISPLAY_NAME};
    private static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Welche Spalte wird in welcher View angezeigt?
        String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Loader vorbereiten. Entweder vorhandenen nutzen, oder
        // einen neuen starten
        getLoaderManager().initLoader(0, null, this);
    }

    // wird aufgerufen, wenn ein neuer Loader erzeugt
    // werden muss
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }

    // wird aufgerufen, wenn ein Loader mit dem Laden fertig ist
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    // wird aufgerufen, wenn die Daten eines Loaders ungültig
    // geworden sind
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Cursor c = (Cursor) getListView().getItemAtPosition(position);
        Uri uri = ContactsContract.Contacts.getLookupUri(
                c.getLong(0), c.getString(1));
        intent.setData(uri);
        startActivity(intent);
    }
}
