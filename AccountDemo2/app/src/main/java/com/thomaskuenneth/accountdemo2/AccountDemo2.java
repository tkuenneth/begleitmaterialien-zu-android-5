package com.thomaskuenneth.accountdemo2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class AccountDemo2 extends Activity implements
        AccountManagerCallback<Bundle> {

    // Konto-Typ
    private static final String TYPE = "com.google";
    // wird bei der Ermittlung des Auth Tokens benötigt
    private static final String AUTH_TOKEN_TYPE = "cl";

    // Schlüssel unter https://console.developers.google.com/ anlegen
    private static final String API_KEY = "<Ihr API-Schlüssel>";

    private static final String TAG = AccountDemo2.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(TYPE);
        if (accounts.length == 1) {
            Bundle options = new Bundle();
            accountManager.getAuthToken(accounts[0], AUTH_TOKEN_TYPE, options,
                    this, this, null);
        } else {
            finish();
        }
    }

    @Override
    public void run(AccountManagerFuture<Bundle> future) {
        Bundle result;
        try {
            result = future.getResult();
            String token = result.getString(AccountManager.KEY_AUTHTOKEN);
            String tasks = getFromServer(
                    "https://www.googleapis.com/tasks/v1/lists/@default/tasks?pp=1&key="
                            + API_KEY, token);
            Log.d(TAG, tasks);
        } catch (Exception e) { // OperationCanceledException, AuthenticatorException, IOException
            Log.e(TAG, "run()", e);
        }
    }

    private String getFromServer(String url, String token) {
        StringBuilder sb = new StringBuilder();
        HttpGet get = new HttpGet(url);
        // Auth Token in den Header übertragen
        get.addHeader("Authorization", "GoogleLogin auth=" + token);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse r;
        try {
            // http-get
            r = httpclient.execute(get);
            InputStream is = r.getEntity().getContent();
            int ch;
            // Daten laden
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (IOException e) { // ClientProtocolException
            Log.e(TAG, "getFromServer()", e);
        }
        return sb.toString();
    }
}