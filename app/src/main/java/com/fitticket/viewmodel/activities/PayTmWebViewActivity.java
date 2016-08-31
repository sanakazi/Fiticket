package com.fitticket.viewmodel.activities;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fitticket.R;

public class PayTmWebViewActivity extends AppCompatActivity {

    WebView paytm_web_view;

    String urlToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_tm_web_view);

        urlToLoad = getIntent().getStringExtra("URL TO LOAD");

        paytm_web_view = (WebView) findViewById(R.id.paytm_web_view);
        paytm_web_view.setWebViewClient(new MyBrowser());
        paytm_web_view.getSettings().setLoadsImagesAutomatically(true);
        paytm_web_view.getSettings().setJavaScriptEnabled(true);
        paytm_web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        paytm_web_view.loadUrl(urlToLoad);


    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("PAYTM URL: ", "PAYTM URL: " + url);
            return false;

        }

        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
}
