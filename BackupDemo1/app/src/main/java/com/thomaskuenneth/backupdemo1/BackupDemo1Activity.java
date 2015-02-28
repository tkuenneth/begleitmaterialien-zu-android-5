package com.thomaskuenneth.backupdemo1;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class BackupDemo1Activity extends Activity {

    public static final String
            NAME = BackupDemo1Activity.class.getSimpleName();

    private static final String EDITTEXT = "edittext";
    private static final String CHECKBOX = "checkbox";

    private SharedPreferences prefs;

    private EditText edittext;
    private CheckBox checkbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        edittext = (EditText) findViewById(R.id.edittext);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Editor editor = prefs.edit();
        editor.putString(EDITTEXT, edittext.getText().toString());
        editor.putBoolean(CHECKBOX, checkbox.isChecked());
        editor.apply();
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(NAME, MODE_MULTI_PROCESS);
        edittext.setText(prefs.getString(EDITTEXT, ""));
        checkbox.setChecked(prefs.getBoolean(CHECKBOX, false));
    }
}