package com.thomaskuenneth.alarmclockdemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;

public class AlarmClockDemo1 extends Activity {

	private static final String TAG = AlarmClockDemo1.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		if ((i != null) && (AlarmClock.ACTION_SET_ALARM.equals(i.getAction()))) {
			Log.d(TAG,
					"EXTRA_HOUR: " + i.getIntExtra(AlarmClock.EXTRA_HOUR, -1));
			Log.d(TAG,
					"EXTRA_MINUTES: "
							+ i.getIntExtra(AlarmClock.EXTRA_MINUTES, -1));
			Log.d(TAG,
					"EXTRA_MESSAGE: "
							+ i.getStringExtra(AlarmClock.EXTRA_MESSAGE));
			// Alarm setzen
            // ... von Ihnen zu implementierende Programmlogik
			// beenden
			finish();
		} else {
			// einen neuen Alarm setzen
			Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
			intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Hallo Android");
			intent.putExtra(AlarmClock.EXTRA_HOUR, 20);
			intent.putExtra(AlarmClock.EXTRA_MINUTES, 0);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
            startActivity(intent);
			finish();
		}
	}
}