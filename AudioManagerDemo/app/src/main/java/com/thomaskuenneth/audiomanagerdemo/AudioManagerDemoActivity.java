package com.thomaskuenneth.audiomanagerdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;

public class AudioManagerDemoActivity extends Activity {

    private static final String TAG = AudioManagerDemoActivity.class.getSimpleName();
    private static final int RQ_RECEIVER = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaSession session = new MediaSession(this, TAG);
        Intent i = new Intent(this, MediaButtonEventReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, RQ_RECEIVER, i, PendingIntent.FLAG_UPDATE_CURRENT);
        session.setMediaButtonReceiver(pi);
    }
}