package com.thomaskuenneth.audiomanagerdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonEventReceiver extends BroadcastReceiver {

    private static final String TAG = MediaButtonEventReceiver.class
            .getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
                KeyEvent keyEvent = intent
                        .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (KeyEvent.ACTION_UP == keyEvent.getAction()) {
                    Log.d(TAG, "getKeyCode(): " + keyEvent.getKeyCode());
                }
                if (keyEvent.isLongPress()) {
                    Log.d(TAG, "laaange gedrückt");
                }
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                Log.d(TAG, "ACTION_AUDIO_BECOMING_NOISY");
            }
        }
    }
}
