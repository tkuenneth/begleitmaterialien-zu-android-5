/*
 * This file is part of C64 Tribute Watch Face
 * Copyright (C) 2014  Thomas Kuenneth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.thomaskuenneth.c64watchface;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * This class represents the settings activity for the watch face on the wearable device.
 *
 * @author Thomas Kuenneth
 */
public class C64WatchFaceWearableConfigActivity extends Activity {

    private static final String TAG = "WearableCfgActivity";

    private GoogleApiClient mGoogleApiClient;

    private CheckBox checkboxSecondsVisible;
    private CheckBox checkboxDateVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_config);

        checkboxSecondsVisible = (CheckBox) findViewById(R.id.checkbox_econds);
        checkboxDateVisible = (CheckBox) findViewById(R.id.checkbox_date);
        CompoundButton.OnCheckedChangeListener l = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateConfigDataItem(checkboxSecondsVisible.isChecked(), checkboxDateVisible.isChecked());
            }
        };
        checkboxSecondsVisible.setOnCheckedChangeListener(l);
        checkboxDateVisible.setOnCheckedChangeListener(l);

        mGoogleApiClient = new Builder(this)
                .addConnectionCallbacks(new ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle connectionHint) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "onConnected: " + connectionHint);
                        }
                        C64WatchFaceUtil.fetchConfigDataMap(mGoogleApiClient,
                                new C64WatchFaceUtil.FetchConfigDataMapCallback() {
                                    @Override
                                    public void onConfigDataMapFetched(DataMap startupConfig) {
                                        if (startupConfig.containsKey(C64WatchFaceUtil.KEY_SECONDS_VISIBLE)) {
                                            checkboxSecondsVisible.setChecked(startupConfig.getBoolean(C64WatchFaceUtil.KEY_SECONDS_VISIBLE));
                                        }
                                        if (startupConfig.containsKey(C64WatchFaceUtil.KEY_DATE_VISIBLE)) {
                                            checkboxDateVisible.setChecked(startupConfig.getBoolean(C64WatchFaceUtil.KEY_DATE_VISIBLE));
                                        }
                                    }
                                }
                        );
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "onConnectionSuspended: " + cause);
                        }
                    }
                })
                .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "onConnectionFailed: " + result);
                        }
                    }
                })
                .addApi(Wearable.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void updateConfigDataItem(final boolean seconds, final boolean dateVisible) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(C64WatchFaceUtil.KEY_SECONDS_VISIBLE,
                seconds);
        configKeysToOverwrite.putBoolean(C64WatchFaceUtil.KEY_DATE_VISIBLE,
                dateVisible);
        C64WatchFaceUtil.overwriteKeysInConfigDataMap(mGoogleApiClient, configKeysToOverwrite);
    }
}
