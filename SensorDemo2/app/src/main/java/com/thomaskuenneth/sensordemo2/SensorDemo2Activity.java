package com.thomaskuenneth.sensordemo2;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

//import org.openintents.sensorsimulator.hardware.Sensor;
//import org.openintents.sensorsimulator.hardware.SensorEvent;
//import org.openintents.sensorsimulator.hardware.SensorEventListener;
//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorDemo2Activity extends Activity {

    private static final String TAG = SensorDemo2Activity.class.getSimpleName();

    private TextView textview;
    private SensorManager manager;
    // private SensorManagerSimulator manager;
    private Sensor sensor;
    private SensorEventListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        setContentView(R.layout.main);
        textview = (TextView) findViewById(R.id.textview);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // manager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
        // manager.connectSimulator();
        sensor = manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        // sensor = manager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        if (sensor != null) {
            listener = new SensorEventListener() {
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    Log.d(TAG, "onAccuracyChanged(): " + accuracy);
                }

                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values.length > 0) {
                        float light = event.values[0];
                        String text = Float.toString(light);
                        textview.setText(text);
                    }
                }
            };
            manager.registerListener(listener, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d(TAG, "kein Temperatursensor vorhanden");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensor != null) {
            manager.unregisterListener(listener);
        }
    }
}