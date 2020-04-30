package com.example.remotecarcontrol;


import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;


public class MyWebStreamConnection extends AppCompatActivity {
    public static WebView connectWebView(WebView webView, String ip){
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://" + ip);
        webSettings.setBuiltInZoomControls(false);
        webView.setInitialScale(100);
        return webView;
    }
}
