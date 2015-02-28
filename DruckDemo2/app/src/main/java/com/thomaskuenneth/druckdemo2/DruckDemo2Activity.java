package com.thomaskuenneth.druckdemo2;

import android.app.Activity;
import android.os.Bundle;
import android.print.PrintManager;


public class DruckDemo2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Document";
        printManager.print(jobName, new DemoPrintDocumentAdapter(this), null);
    }
}
