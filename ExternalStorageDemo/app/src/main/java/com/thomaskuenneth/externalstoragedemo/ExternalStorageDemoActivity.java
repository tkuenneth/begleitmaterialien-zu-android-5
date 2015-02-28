package com.thomaskuenneth.externalstoragedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExternalStorageDemoActivity extends Activity {

    private static final String TAG = ExternalStorageDemoActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // kann externes Medium entfernt werden?
        Log.d(TAG, "Medium kann"
                + (Environment.isExternalStorageRemovable() ? "" : " nicht")
                + " entfernt werden");
        // Status abfragen
        final String state = Environment.getExternalStorageState();
        final boolean canRead;
        final boolean canWrite;
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // lesen und schreiben möglich
                canRead = true;
                canWrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // lesen möglich, schreiben nicht möglich
                canRead = true;
                canWrite = false;
                break;
            default:
                // lesen und schreiben nicht möglich
                canRead = false;
                canWrite = false;
        }
        Log.d(TAG, "Lesen ist" + (canRead ? "" : " nicht") + " möglich");
        Log.d(TAG, "Schreiben ist" + (canWrite ? "" : " nicht") + " möglich");
        // Wurzelverzeichnis des externen Mediums
        File dirBase = Environment.getExternalStorageDirectory();
        Log.d(TAG, "getExternalStorageDirectory(): " + dirBase.getAbsolutePath());
        // App-spezifischen Pfad hinzufügen
        File dirAppBase = new File(dirBase.getAbsolutePath() + File.separator
                + "Android" + File.separator + "data" + File.separator
                + getClass().getPackage().getName() + File.separator + "files");
        // ggf. Verzeichnisse anlegen
        if (!dirAppBase.mkdirs()) {
            Log.d(TAG, "alle Unterverzeichnisse von " + dirAppBase.getAbsolutePath() + " schon vorhanden");
        }
        // App-spezifisches Basisverzeichnis erfragen
        Log.d(TAG, "getExternalFilesDir(null): " +
                getExternalFilesDir(null).getAbsolutePath());
        // App-spezifisches Verzeichnis für Bilder erfragen
        Log.d(TAG, "getExternalFilesDir(Environment.DIRECTORY_PICTURES): " +
                getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        // Pfad auf öffentliches Verzeichnis für Bilder
        File dirPublicPictures = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // ggf. Verzeichnisse anlegen
        if (!dirPublicPictures.mkdirs()) {
            Log.d(TAG, "alle Unterverzeichnisse von " + dirPublicPictures.getAbsolutePath() + " schon vorhanden");
        }
        // Grafik erzeugen und speichern
        FileOutputStream fos = null;
        try {
            File file = new File(dirPublicPictures, "grafik.png");
            fos = new FileOutputStream(file);
            saveBitmap(fos);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "new FileOutputStream()", e);
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

    private void saveBitmap(OutputStream out) {
        // Grafik erzeugen
        int w = 100;
        int h = 100;
        Bitmap bm = Bitmap.createBitmap(w, h, Config.RGB_565);
        Canvas c = new Canvas(bm);
        Paint paint = new Paint();
        paint.setTextAlign(Align.CENTER);
        paint.setColor(Color.WHITE);
        c.drawRect(0, 0, w - 1, h - 1, paint);
        paint.setColor(Color.BLUE);
        c.drawLine(0, 0, w - 1, h - 1, paint);
        c.drawLine(0, h - 1, w - 1, 0, paint);
        paint.setColor(Color.BLACK);
        c.drawText("Hallo Android!", w / 2, h / 2, paint);
        // und speichern
        bm.compress(CompressFormat.PNG, 100, out);
    }
}