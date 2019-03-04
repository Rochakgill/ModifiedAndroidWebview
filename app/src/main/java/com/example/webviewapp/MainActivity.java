package com.example.webviewapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "webview";

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CookieSyncManager.createInstance(getBaseContext());

        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setAppCacheEnabled( true );
        webSettings.setJavaScriptEnabled(true);

        CookieSyncManager.getInstance().startSync();
        CookieSyncManager.getInstance().sync();


        if (!isNetworkAvailable()) {
            loadErrorPage();
        } else {
            myWebView.loadUrl("http://www.facebook.com");
        }
        myWebView.setWebViewClient(new CustomWebViewClient());
    }


    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void loadErrorPage() {
        Intent errorIntent = new Intent(this, connectionError.class);
        startActivity(errorIntent);
    }


    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isNetworkAvailable()) {
                Log.e(TAG, "shouldOverrideUrlLoading: reached here otherlink");
                buildDialog(MainActivity.this).show();
            } else {
                Log.e(TAG, "shouldOverrideUrlLoading: reached here loaded");
                view.loadUrl(url);
            }
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            CookieSyncManager.getInstance().sync();
        }
    }

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkAvailable()) dialog.cancel();
                else buildDialog(MainActivity.this).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }



}
