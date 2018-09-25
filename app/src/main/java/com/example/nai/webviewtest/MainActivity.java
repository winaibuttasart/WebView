package com.example.nai.webviewtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private WebView webview;

    private static final int REQUEST_CALL_PHONE = 1;
    private String phoneNumber = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        MyWebViewClient myWebViewClient = new MyWebViewClient(this);
        webview.setWebViewClient(myWebViewClient);
        webview.loadUrl("http://192.168.23.65:5500/test.html");
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("onConsoleMessage", "lineNumber === " + consoleMessage.lineNumber());
                Log.i("onConsoleMessage", "message === " + consoleMessage.message());
                Log.i("onConsoleMessage", "sourceId === " + consoleMessage.sourceId());
                String message[] = consoleMessage.message().split(":");
                switch (message[0]) {
                    case "line":
                        Uri uri = Uri.parse("https://line.me/R/ti/p/%40taxibeamdriver");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case "tel":
                        phoneNumber = message[1];
                        startTel();
                        break;
                    case "exit":
                        finish();
                        break;
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    private void startTel() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTel();
                }
            }

        }

    }

}
