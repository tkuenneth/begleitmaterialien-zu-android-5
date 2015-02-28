package com.thomaskuenneth.backupdemo3;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class BackupDemo3Activity extends Activity {

    public static final Object[] LOCK = new Object[0];
    public static final String INPUT1 = "input1";
    public static final String INPUT2 = "input2";

    private static final String TAG = BackupDemo3Activity.class.getSimpleName();

    private EditText input1, input2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final BackupManager bm = new BackupManager(this);
        setContentView(R.layout.main);
        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        final Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put(INPUT1, input1.getText().toString());
                    object.put(INPUT2, input2.getText().toString());
                    FileUtilities.save(BackupDemo3Activity.this, object);
                    // Sicherung anfordern
                    bm.dataChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Problem mit der JSON API", e);
                }
                // Activity beenden
                finish();
            }
        });
        JSONObject object = FileUtilities.load(this);
        if (object != null) {
            try {
                input1.setText(object.getString(INPUT1));
                input2.setText(object.getString(INPUT2));
            } catch (JSONException e) {
                Log.e(TAG, "Problem mit der JSON API", e);
            }
        }
    }
}