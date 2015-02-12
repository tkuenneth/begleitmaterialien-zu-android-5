package com.thomaskuenneth.uhrzeitlivewallpaper;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.Calendar;

public class UhrzeitLiveWallpaperService extends WallpaperService {

    private final Handler handler = new Handler();

    @Override
    public Engine onCreateEngine() {
        return new UhrzeitLiveWallpaperEngine();
    }

    private class UhrzeitLiveWallpaperEngine extends Engine {

        private final Paint paint;
        private final Runnable runnable;
        private final Calendar cal;

        private int color;
        private float x, y;
        private boolean visible;

        UhrzeitLiveWallpaperEngine() {
            paint = new Paint();
            runnable = new Runnable() {
                public void run() {
                    draw();
                }
            };
            cal = Calendar.getInstance();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(runnable);
        }

        @Override
        public void onVisibilityChanged(boolean v) {
            visible = v;
            if (v) {
                draw();
            } else {
                handler.removeCallbacks(runnable);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            color = Color.WHITE;
            x = width / 2.0f;
            y = height / 2.0f;
            draw();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            visible = false;
            handler.removeCallbacks(runnable);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                x = event.getX();
                y = event.getY();
                draw();
            }
        }

        void draw() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    int w = c.getWidth();
                    int h = c.getHeight();
                    paint.setColor(Color.BLACK);
                    c.drawRect(0, 0, w - 1, h - 1, paint);
                    cal.setTimeInMillis(System.currentTimeMillis());
                    paint.setColor(color);
                    paint.setTextSize(h / 10);
                    paint.setTextAlign(Align.RIGHT);
                    c.drawText(String.format("%tH", cal), x, y, paint);
                    color = getNextColor(color);
                    paint.setColor(color);
                    paint.setTextSize(h / 13);
                    paint.setTextAlign(Align.LEFT);
                    c.drawText(String.format("%tM", cal), x, y, paint);
                    color = getNextColor(color);
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
            handler.removeCallbacks(runnable);
            if (visible) {
                SharedPreferences prefs =
                        PreferenceManager
                                .getDefaultSharedPreferences(
                                        UhrzeitLiveWallpaperService.this);
                String s = prefs.getString("refresh", "3000");
                handler.postDelayed(runnable, Integer.parseInt(s));
            }
        }

        private int getNextColor(int color) {
            if (color == Color.WHITE) {
                return Color.BLUE;
            } else if (color == Color.BLUE) {
                return Color.RED;
            } else if (color == Color.RED) {
                return Color.GREEN;
            } else {
                return Color.WHITE;
            }
        }
    }
}
