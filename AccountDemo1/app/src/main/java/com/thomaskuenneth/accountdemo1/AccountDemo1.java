package com.thomaskuenneth.accountdemo1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AccountDemo1 extends Activity {

    private static final String TAG = AccountDemo1.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        Log.d(TAG, "Anzahl gefundener Konten: " + accounts.length);
        for (Account account : accounts) {
            Log.d(TAG, account.toString());
        }
        finish();
    }
}