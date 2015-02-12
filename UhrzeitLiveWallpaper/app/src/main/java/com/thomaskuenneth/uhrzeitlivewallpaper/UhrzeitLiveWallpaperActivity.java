package com.thomaskuenneth.uhrzeitlivewallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UhrzeitLiveWallpaperActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(
                        WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                startActivity(intent);
                finish();
            }
        });
    }
}
