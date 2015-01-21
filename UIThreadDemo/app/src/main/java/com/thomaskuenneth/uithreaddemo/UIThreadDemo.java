package com.thomaskuenneth.uithreaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class UIThreadDemo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView textview = (TextView) findViewById(R.id.textview);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textview.setText(UIThreadDemo.this.getString(R.string.begin));
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                }
                // for (int i = 0; i < 1;);
                textview.setText(UIThreadDemo.this.getString(R.string.end));
            }
        });
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                textview.setText(Boolean.toString(isChecked));
            }
        });
        checkbox.setChecked(true);
    }
}