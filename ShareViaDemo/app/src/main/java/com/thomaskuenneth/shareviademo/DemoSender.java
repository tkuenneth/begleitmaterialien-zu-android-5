package com.thomaskuenneth.shareviademo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class DemoSender extends Activity {

    private static final String TAG = DemoSender.class.getSimpleName();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Wurde ein Intent übermittelt?
        Intent intent = getIntent();
        if (intent != null) {
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                // ja, dann Benutzeroberfläche anzeigen
                setContentView(R.layout.main);
                ImageView imageView = (ImageView) findViewById(R.id.image);
                // Uri des Bildes
                final Uri imageUri = (Uri) intent.getExtras().get(
                        Intent.EXTRA_STREAM);
                // Button
                final Button button = (Button) findViewById(R.id.button);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share(imageUri);
                    }
                });
                try {
                    // Bitmap erzeugen
                    Bitmap bm1 = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    // in Graustufen umwandeln
                    Bitmap bm2 = toGrayscale(bm1);
                    // und anzeigen
                    imageView.setImageBitmap(bm2);
                } catch (IOException e) { // FileNotFoundException
                    Log.e(TAG, e.getClass().getSimpleName(), e);
                }
            }
        } else {
            // nein, dann beenden
            finish();
        }
    }

    private Bitmap toGrayscale(Bitmap src) {
        // Breite und Höhe
        int width = src.getWidth();
        int height = src.getHeight();
        // neue Bitmap erzeugen
        Bitmap desti = Bitmap
                .createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(desti);
        Paint paint = new Paint();
        // Umwandlung in Graustufen
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        // mit Filter kopieren
        c.drawBitmap(src, 0, 0, paint);
        return desti;
    }

    private void share(Uri uri) {
        // das zu feuernde Intent bauen
        Intent target = new Intent(Intent.ACTION_SEND, uri);
        target.setType("image/?");
        // Activity-Chooser-Intent bauen und feuern
        Intent intent = Intent.createChooser(target,
                getString(R.string.share_via));
        startActivity(intent);
    }
}
