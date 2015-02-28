package com.thomaskuenneth.backupdemo3;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StructureBackupAgent extends BackupAgent {

    private static final String TAG = StructureBackupAgent.class
            .getSimpleName();

    private File mDataFile;

    @Override
    public void onCreate() {
        mDataFile = FileUtilities.getFile(this);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Änderungsdatum der lokalen Datei ermitteln
        long fileModified = mDataFile.lastModified();
        // Dateidatum während des letzten Backups ermitteln
        long stateModified = 0L;
        try {
            if (oldState != null) {
                FileInputStream instream = new FileInputStream(
                        oldState.getFileDescriptor());
                DataInputStream in = new DataInputStream(instream);
                try {
                    stateModified = in.readLong();
                } catch (IOException e) {
                    Log.e(TAG, "Fehler beim Lesen", e);
                    // wir ignorieren diese Ausnahme und erzwingen damit ein
                    // Backup
                }
            }
            if ((stateModified != fileModified) || (stateModified == 0l)) {
                // lokale Datei ist vorhanden und wurde geändert, also Backup
                // durchführen
                JSONObject object = FileUtilities.load(this);
                try {
                    byte[] b1 = object.getString(BackupDemo3Activity.INPUT1)
                            .getBytes();
                    int l1 = b1.length;
                    byte[] b2 = object.getString(BackupDemo3Activity.INPUT2)
                            .getBytes();
                    int l2 = b2.length;
                    // Daten an Backup Manager senden
                    data.writeEntityHeader(BackupDemo3Activity.INPUT1, l1);
                    data.writeEntityData(b1, l1);
                    data.writeEntityHeader(BackupDemo3Activity.INPUT2, l2);
                    data.writeEntityData(b2, l2);
                } catch (JSONException e) {
                    Log.e(TAG, "Problem mit der JSON API", e);
                }
            } else {
                Log.d(TAG, "keine Änderung an Datei");
                // Datei hat sich nicht geändert - kein Backup
                // ggf. Aufräumaktionen durchführen
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem beim Zugriff auf Dateien", e);
            // Google rät, trotzdem ein Backup zu machen; wurde hier nicht
            // ausprogrammiert
        } finally {
            updateTimestamp(newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        Log.d(TAG, "App-Version, mit der Backup erstellt wurde: "
                + appVersionCode);
        Log.d(TAG, "Aktuelle Version der App: " + getVersionCode());
        JSONObject object = new JSONObject();
        while (data.readNextHeader()) {
            String key = data.getKey();
            int dataSize = data.getDataSize();
            // Backup-Datensatz lesen
            byte[] dataBuf = new byte[dataSize];
            data.readEntityData(dataBuf, 0, dataSize);
            String s = new String(dataBuf);
            // Ist uns der Schlüssel bekannt?
            try {
                if (BackupDemo3Activity.INPUT1.equals(key)) {
                    object.put(BackupDemo3Activity.INPUT1, s);
                } else if (BackupDemo3Activity.INPUT2.equals(key)) {
                    object.put(BackupDemo3Activity.INPUT2, s);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem mit der JSON API", e);
            }
        }
        FileUtilities.save(this, object);
        updateTimestamp(newState);
    }

    private void updateTimestamp(ParcelFileDescriptor newState) {
        FileOutputStream outstream = new FileOutputStream(
                newState.getFileDescriptor());
        DataOutputStream out = new DataOutputStream(outstream);
        try {
            out.writeLong(mDataFile.lastModified());
        } catch (IOException e) {
            Log.e(TAG, "Fehler beim Schreiben des Zeitstempels", e);
        } finally {
            try {
                out.close();
                outstream.close();
            } catch (IOException e) {
                Log.e(TAG, "close()", e);
            }
        }
    }

    private int getVersionCode() {
        PackageInfo info;
        try {
            String name = getPackageName();
            info = getPackageManager().getPackageInfo(name, 0);
        } catch (NameNotFoundException nnfe) {
            info = null;
        }
        int versionCode = 0;
        if (info != null) {
            versionCode = info.versionCode;
        }
        return versionCode;
    }
}
