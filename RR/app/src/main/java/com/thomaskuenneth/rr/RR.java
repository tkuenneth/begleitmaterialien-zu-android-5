package com.thomaskuenneth.rr;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

public class RR extends Activity {

    private static final String TAG = RR.class.getSimpleName();

    // wird für die Schaltfläche benötigt
    private static enum MODE {
        WAITING, RECORDING, PLAYING
    }

    private MODE mode;

    // Bedienelemente der App
    private RRListAdapter listAdapter;
    private Button b;

    // Datei mit der aktuellen Aufnahme
    private File currentFile;

    private MediaPlayer player;
    private MediaRecorder recorder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // ListView initialisieren
        final ListView lv = (ListView) findViewById(R.id.listview);
        listAdapter = new RRListAdapter(this);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Datei wiedergeben
                File f = listAdapter.getItem(position);
                playAudioFile(f.getAbsolutePath());
            }
        });
        // Schaltfläche Aufnehmen/Beenden initialisieren
        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MODE.WAITING) {
                    currentFile = recordToFile();
                } else if (mode == MODE.RECORDING) {
                    // die Aufnahme stoppen
                    recorder.stop();
                    releaseRecorder();
                    listAdapter.add(currentFile);
                    currentFile = null;
                    mode = MODE.WAITING;
                    updateButtonText();
                } else if (mode == MODE.PLAYING) {
                    player.stop();
                    releasePlayer();
                    mode = MODE.WAITING;
                    updateButtonText();
                }
            }
        });
        currentFile = null;
        mode = MODE.WAITING;
        player = null;
        recorder = null;
        updateButtonText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        releaseRecorder();
    }

    private void updateButtonText() {
        b.setText(getString((mode != MODE.WAITING) ? R.string.finish
                : R.string.record));
    }

    private File recordToFile() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File f = new RRFile(getBaseDir(), Long.toString(System
                .currentTimeMillis()) + RRFile.EXT_3GP);
        try {
            if (!f.createNewFile()) {
                Log.d(TAG, "Datei schon vorhanden");
            }
            recorder.setOutputFile(f.getAbsolutePath());
            recorder.prepare();
            recorder.start();
            mode = MODE.RECORDING;
            updateButtonText();
            return f;
        } catch (IOException e) {
            Log.e(TAG, "Konnte Aufnahme nicht starten", e);
        }
        return null;
    }

    private void releaseRecorder() {
        if (recorder != null) {
            // Recorder-Ressourcen freigeben
            recorder.release();
            recorder = null;
        }
    }

    private void playAudioFile(String filename) {
        player = new MediaPlayer();
        player.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer player) {
                releasePlayer();
                mode = MODE.WAITING;
                updateButtonText();
            }
        });
        try {
            player.setDataSource(filename);
            player.prepare();
            player.start();
            mode = MODE.PLAYING;
            updateButtonText();
        } catch (Exception thr) {
            // IllegalArgumentException, IllegalStateException, IOException
            Log.e(TAG, "konnte Audio nicht wiedergeben", thr);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            // Player-Ressourcen freigeben
            player.release();
            player = null;
        }
    }

    public static File getBaseDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), ".RR");
        // ggf. Verzeichnisse anlegen
        if (!dir.mkdirs()) {
            Log.d(TAG, "Verzeichnisse schon vorhanden");
        }
        return dir;
    }
}