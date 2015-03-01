package com.thomaskuenneth.tkmoodley;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TKMoodleyOpenHandler extends SQLiteOpenHelper {

	private static final String TAG = TKMoodleyOpenHandler.class
			.getSimpleName();

	private static final String DATABASE_NAME = "tkmoodley.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME_MOOD = "mood";
	public static final String _ID = "_id";
	public static final String MOOD_TIME = "timeMillis";
	public static final String MOOD_MOOD = "mood";

	public static final int MOOD_FINE = 1;
	public static final int MOOD_OK = 2;
	public static final int MOOD_BAD = 3;

	private static final String TABLE_MOOD_CREATE = "CREATE TABLE "
			+ TABLE_NAME_MOOD + " (" + _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MOOD_TIME + " INTEGER, "
			+ MOOD_MOOD + " INTEGER);";

	private static final String TABLE_MOOD_DROP = "DROP TABLE IF EXISTS "
			+ TABLE_NAME_MOOD;

	TKMoodleyOpenHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_MOOD_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrade der Datenbank von Version " + oldVersion + " zu "
				+ newVersion + "; alle Daten werden gelöscht");
		db.execSQL(TABLE_MOOD_DROP);
		onCreate(db);
	}

	public void insert(int mood, long timeMillis) {
		long rowId = -1;
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(MOOD_MOOD, mood);
			values.put(MOOD_TIME, timeMillis);
			rowId = db.insert(TABLE_NAME_MOOD, null, values);
		} catch (SQLiteException e) {
			Log.e(TAG, "insert()", e);
		} finally {
			Log.d(TAG, "insert(): rowId=" + rowId);
		}
	}

	public Cursor query() {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(TABLE_NAME_MOOD, null, null, null, null, null,
				MOOD_TIME + " DESC");
	}

	public void update(long id, int smiley) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MOOD_MOOD, smiley);
		int numUpdated = db.update(TABLE_NAME_MOOD, values, _ID + " = ?",
				new String[] { Long.toString(id) });
		Log.d(TAG, "update(): id=" + id + " -> " + numUpdated);
	}

	public void delete(long id) {
		SQLiteDatabase db = getWritableDatabase();
		int numDeleted = db.delete(TABLE_NAME_MOOD, _ID + " = ?",
				new String[] { Long.toString(id) });
		Log.d(TAG, "delete(): id=" + id + " -> " + numDeleted);
	}
}
