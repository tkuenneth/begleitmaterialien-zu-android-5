package com.thomaskuenneth.wallpaperdemo;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class WallpaperDemo extends Activity {

    private static final String TAG = WallpaperDemo.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if ((intent == null)
                || (!Intent.ACTION_SET_WALLPAPER.equals(intent.getAction()))) {
            Intent target = new Intent(Intent.ACTION_SET_WALLPAPER);
            startActivity(Intent.createChooser(target,
                    getString(R.string.choose)));
        } else {
            WallpaperManager m = WallpaperManager.getInstance(this);
            int w = m.getDesiredMinimumWidth();
            int h = m.getDesiredMinimumHeight();
            Bitmap bm = Bitmap.createBitmap(w, h, Config.RGB_565);
            Canvas c = new Canvas(bm);
            Paint paint = new Paint();
            paint.setTextAlign(Align.CENTER);
            paint.setColor(Color.WHITE);
            paint.setTextSize(64);
            c.drawRect(0, 0, w - 1, h - 1, paint);
            paint.setColor(Color.BLUE);
            c.drawLine(0, 0, w - 1, h - 1, paint);
            c.drawLine(0, h - 1, w - 1, 0, paint);
            paint.setColor(Color.BLACK);
            c.drawText(getString(R.string.hello), w / 2, h / 2, paint);
            try {
                m.setBitmap(bm);
            } catch (IOException e) {
                Log.e(TAG, "Fehler bei setBitmap()", e);
            }
            // "einfach nur" eine Grafik anzeigen
//            try {
//                m.setResource(R.drawable.icon);
//            } catch (IOException e) {
//                Log.e(TAG, "Fehler bei setResource()", e);
//            }
        }
        finish();
    }
}