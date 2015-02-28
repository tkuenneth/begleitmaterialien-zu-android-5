package com.thomaskuenneth.filedemo2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDemo2Activity extends Activity {

    private static final String TAG = FileDemo2Activity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 10 Dateien mit unterschiedlicher Länge anlegen
        for (int i = 1; i <= 10; i++) {
            FileOutputStream fos = null;
            String name = null;
            try {
                // ergibt Datei_1, Datei_2, ...
                name = "Datei_" + Integer.toString(i);
                fos = openFileOutput(name, MODE_PRIVATE);
                // ein Feld der Länge i mit dem Wert i füllen
                byte[] bytes = new byte[i];
                for (int j = 0; j < bytes.length; j++) {
                    bytes[j] = (byte) i;
                }
                fos.write(bytes);
            } catch (IOException t) {
                Log.e(TAG, name, t);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, "close()", e);
                    }
                }
            }
        }
        // Dateien ermitteln
        String[] files = fileList();
        // Verzeichnis ermitteln
        File dir = getFilesDir();
        for (String name : files) {
            File f = new File(dir, name);
            // Länge in Bytes ermitteln
            Log.d(TAG, "Länge von " + name + " in Byte: " + f.length());
            // Datei löschen
            Log.d(TAG, "Löschen " + (!f.delete() ? "nicht " : "")
                    + "erfolgreich");
        }
    }
}