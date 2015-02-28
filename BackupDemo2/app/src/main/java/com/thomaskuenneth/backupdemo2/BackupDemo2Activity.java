package com.thomaskuenneth.backupdemo2;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BackupDemo2Activity extends Activity {

    private static final String TAG = BackupDemo2Activity.class.getSimpleName();

    // wird benötigt, um Zugriffe auf die Datei zu synchronisieren
    public static final Object[] LOCK = new Object[0];

    public static final String DATEINAME = "BackupDemo2Activity.txt";

    private EditText edittext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final BackupManager bm = new BackupManager(this);
        setContentView(R.layout.main);
        edittext = (EditText) findViewById(R.id.edittext);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
                bm.dataChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void save() {
        try {
            synchronized (BackupDemo2Activity.LOCK) {
                File dataFile = new File(getFilesDir(), DATEINAME);
                RandomAccessFile raFile = new RandomAccessFile(dataFile, "rw");
                raFile.writeUTF(edittext.getText().toString());
                raFile.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fehler beim Schreiben der Datei " + DATEINAME);
        }
    }

    private void load() {
        try {
            synchronized (BackupDemo2Activity.LOCK) {
                File dataFile = new File(getFilesDir(), DATEINAME);
                RandomAccessFile raFile = new RandomAccessFile(dataFile, "r");
                String text = raFile.readUTF();
                edittext.setText(text);
                raFile.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fehler beim Lesen der Datei " + DATEINAME);
        }
    }
}