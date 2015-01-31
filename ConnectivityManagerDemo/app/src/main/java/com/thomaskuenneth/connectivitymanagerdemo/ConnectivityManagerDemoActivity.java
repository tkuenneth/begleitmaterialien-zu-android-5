package com.thomaskuenneth.connectivitymanagerdemo;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;


public class ConnectivityManagerDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.textview);

        ConnectivityManager mgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networks = mgr.getAllNetworkInfo();
        for (NetworkInfo n : networks) {
            tv.append(n.getTypeName() + " ("
                    + n.getSubtypeName() + ")\n");
            tv.append("isAvailable(): " + n.isAvailable() + "\n");
            tv.append("isConnected(): " + n.isConnected() + "\n");
            tv.append("roaming ist " + (n.isRoaming() ? "ein" : "aus") + "\n\n");
        }
    }
}
