package com.thomaskuenneth.subscriptionmanagerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;


public class SubscriptionManagerDemoActivity extends Activity {

    private static final String TAG = SubscriptionManagerDemoActivity.class.getSimpleName();

    private SubscriptionManager m;
    private SubscriptionManager.OnSubscriptionsChangedListener l;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        layout = (LinearLayout) findViewById(R.id.layout);

        m = SubscriptionManager.from(this);
        l = new SubscriptionManager.OnSubscriptionsChangedListener() {

            @Override
            public void onSubscriptionsChanged() {
                Log.d(TAG, "onSubscriptionsChanged()");
                output();
            }
        };
        output();
    }

    @Override
    protected void onPause() {
        m.removeOnSubscriptionsChangedListener(l);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        m.addOnSubscriptionsChangedListener(l);
    }

    private void output() {
        List<SubscriptionInfo> l = m.getActiveSubscriptionInfoList();
        layout.removeAllViews();
        if (l != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (SubscriptionInfo i : l) {
                Log.d(TAG, "getCarrierName(): " + i.getCarrierName().toString());
                Log.d(TAG, "getDisplayName(): " + i.getDisplayName().toString());
                Log.d(TAG, "getDataRoaming(): " + i.getDataRoaming());
                ImageView imageview = new ImageView(this);
                imageview.setLayoutParams(params);
                imageview.setImageBitmap(i.createIconBitmap(this));
                layout.addView(imageview);
            }
        }
        layout.invalidate();
    }
}
