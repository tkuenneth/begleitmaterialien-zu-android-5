package com.thomaskuenneth.backupdemo1;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.util.Log;

public class MyBackupAgent extends BackupAgentHelper {

    private static final String TAG = MyBackupAgent.class.getSimpleName();

    @Override
    public void onCreate() {
        BackupHelper h = new SharedPreferencesBackupHelper(this,
                BackupDemo1Activity.NAME);
        addHelper(TAG, h);
        Log.d(TAG, "BackupHelper registriert");
    }
}
