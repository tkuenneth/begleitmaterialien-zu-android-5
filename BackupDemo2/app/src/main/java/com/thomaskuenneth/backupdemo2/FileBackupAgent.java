package com.thomaskuenneth.backupdemo2;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

public class FileBackupAgent extends BackupAgentHelper {

    static final String BACKUP_KEY = FileBackupAgent.class.getSimpleName();

    @Override
    public void onCreate() {
        FileBackupHelper h = new FileBackupHelper(this,
                BackupDemo2Activity.DATEINAME);
        addHelper(BACKUP_KEY, h);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        synchronized (BackupDemo2Activity.LOCK) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        synchronized (BackupDemo2Activity.LOCK) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}
