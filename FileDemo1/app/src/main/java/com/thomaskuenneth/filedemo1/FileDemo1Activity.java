package com.thomaskuenneth.filedemo1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileDemo1Activity extends Activity {

    private static final String TAG = FileDemo1Activity.class.getSimpleName();
    private static final String FILENAME = TAG + ".txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // das Eingabefeld
        final EditText edit = (EditText) findViewById(R.id.edit);
        // Leeren
        final Button bClear = (Button) findViewById(R.id.clear);
        bClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText("");
            }
        });
        // Laden
        final Button bLoad = (Button) findViewById(R.id.load);
        bLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setText(load());
            }
        });
        // Speichern
        final Button bSave = (Button) findViewById(R.id.save);
        bSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                save(edit.getText().toString());
            }
        });
        // Ablageort der Dateien ermitteln und ausgeben
        File f = getFilesDir();
        Log.d(TAG, "getFilesDir(): " + f.getAbsolutePath());
    }

    private String load() {
        StringBuilder sb = new StringBuilder();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            // kann FileNotFoundException werfen; diese erbt aber von IOException
            fis = openFileInput(FILENAME);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String s;
            // Datei zeilenweise lesen
            while ((s = br.readLine()) != null) {
                // ggf. Zeilenumbruch hinzufügen
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(s);
            }
        } catch (IOException t) {
            Log.e(TAG, "load()", t);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, "br.close()", e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    Log.e(TAG, "isr.close()", e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e(TAG, "fis.close()", e);
                }
            }
        }
        return sb.toString();
    }

    private void save(String s) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            // kann FileNotFoundException werfen; diese erbt aber von IOException
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            osw = new OutputStreamWriter(fos);
            osw.write(s);
        } catch (IOException t) {
            Log.e(TAG, "save()", t);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    Log.e(TAG, "osw.close()", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "fos.close()", e);
                }
            }
        }
    }
}