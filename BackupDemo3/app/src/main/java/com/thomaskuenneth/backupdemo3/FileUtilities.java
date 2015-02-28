package com.thomaskuenneth.backupdemo3;

import android.content.ContextWrapper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtilities {

    private static final String TAG = FileUtilities.class.getSimpleName();
    private static final String DATEINAME = TAG + ".json";

    public static File getFile(ContextWrapper cw) {
        return new File(cw.getFilesDir(), DATEINAME);
    }

    public static void save(ContextWrapper cw, JSONObject object) {
        try {
            synchronized (BackupDemo3Activity.LOCK) {
                File f = new File(cw.getFilesDir(), DATEINAME);
                RandomAccessFile raFile = new RandomAccessFile(f, "rw");
                raFile.writeUTF(object.toString());
                raFile.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fehler beim Schreiben der Datei " + DATEINAME);
        }
    }

    public static JSONObject load(ContextWrapper cw) {
        JSONObject object = null;
        synchronized (BackupDemo3Activity.LOCK) {
            File dataFile = new File(cw.getFilesDir(), DATEINAME);
            RandomAccessFile raFile = null;
            try {
                raFile = new RandomAccessFile(dataFile, "r");
                String data = raFile.readUTF();
                object = new JSONObject(data);
            } catch (JSONException e) {
                Log.e(TAG, "Problem mit der JSON API", e);
            } catch (FileNotFoundException e) {
                Log.e(TAG, DATEINAME + " nicht gefunden", e);
            } catch (IOException e) {
                Log.e(TAG, "Fehler beim Lesen der Datei " + DATEINAME, e);
            } finally {
                try {
                    if (raFile != null) {
                        raFile.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "close()", e);
                }
            }
        }
        return object;
    }
}
