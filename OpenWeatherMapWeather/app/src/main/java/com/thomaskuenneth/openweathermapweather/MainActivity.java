package com.thomaskuenneth.openweathermapweather;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText city;
    private ImageView image;
    private TextView temperatur, beschreibung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Netzwerkverfügbarkeit prüfen
        if (!istNetzwerkVerfuegbar()) {
            finish();
        }

        city = (EditText) findViewById(R.id.city);
        image = (ImageView) findViewById(R.id.image);
        temperatur = (TextView) findViewById(R.id.temperatur);
        beschreibung = (TextView) findViewById(R.id.beschreibung);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final WeatherData weather = WeatherUtils
                                    .getWeather(city.getText().toString());
                            final Bitmap bitmapWeather = WeatherUtils.getImage(weather);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    image.setImageBitmap(bitmapWeather);
                                    beschreibung.setText(weather.description);
                                    city.setText(weather.name);
                                    Double temp = weather.temp - 273.15;
                                    temperatur.setText(getString(R.string.temp_template, temp.intValue()));
                                }

                            });
                        } catch (Exception e /* IOException, MalformedURLException, JSONException */) {
                            Log.e(TAG, "getWeather()", e);
                        }
                    }
                }).start();
            }
        });
    }

    private boolean istNetzwerkVerfuegbar() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
