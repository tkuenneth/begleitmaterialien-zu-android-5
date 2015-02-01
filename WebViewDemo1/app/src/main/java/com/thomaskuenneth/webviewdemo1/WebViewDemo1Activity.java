package com.thomaskuenneth.webviewdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class WebViewDemo1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Webseite mit einem Intent anzeigen
//        Uri uri = Uri.parse("https://www.rheinwerk-verlag.de/");
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);

        WebView webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("https://www.rheinwerk-verlag.de/");

        // URL-Kodierung
//        String html = "<html><body><p>Hallo Android</p></body></html>";
//        webview.loadData(html, "text/html", null);
        // Base64-Kodierung
//        String base64 = Base64.encodeToString(html.getBytes(), Base64.DEFAULT);
//        webview.loadData(base64, "text/html", "base64");
    }
}
