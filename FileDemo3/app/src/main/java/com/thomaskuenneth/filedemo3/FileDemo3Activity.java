package com.thomaskuenneth.filedemo3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDemo3Activity extends Activity {

    private static final String TAG = FileDemo3Activity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // zwei leere Dateien erzeugen
        createFile(getFilesDir(), "A");
        createFile(getFilesDir(), "B");
        // ein Verzeichnis erstellen
        File dirAudio = getDir("audio", MODE_PRIVATE);
        // zwei leere Dateien erzeugen
        createFile(dirAudio, "C");
        createFile(dirAudio, "D");
        // ein Verzeichnis erstellen
        File dirVideo = getDir("video", MODE_PRIVATE);
        // zwei leere Dateien erzeugen
        createFile(dirVideo, "E");
        createFile(dirVideo, "F");
        // temporäre Datei anlegen
        File file = null;
        try {
            Log.d(TAG, System.getProperty("java.io.tmpdir"));
            file = File.createTempFile("Datei_", ".txt");
        } catch (IOException e) {
            Log.e(TAG, " createTempFile()", e);
        } finally {
            if (file != null) {
                Log.d(TAG, file.getAbsolutePath());
            }
        }
        // temporäre Datei im Cache-Verzeichnis
        Log.d(TAG, getCacheDir().getAbsolutePath());
        File file2 = null;
        try {
            file2 = File.createTempFile("Datei_", ".txt", getCacheDir());
        } catch (IOException e) {
            Log.e(TAG, " createTempFile()", e);
        } finally {
            if (file2 != null) {
                Log.d(TAG, file2.getAbsolutePath());
            }
        }
    }

    private void createFile(File dir, String name) {
        FileOutputStream fos = null;
        try {
            File file = new File(dir, name);
            Log.d(TAG, file.getAbsolutePath());
            // Datei anlegen
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openFileOutput()", e);
        } finally {
            // Datei schließen
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "close()", e);
                }
            }
        }
    }
}