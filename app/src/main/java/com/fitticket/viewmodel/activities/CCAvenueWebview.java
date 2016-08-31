package com.fitticket.viewmodel.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.viewmodel.utils.Utilities;

public class CCAvenueWebview extends AppCompatActivity {

	String status = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        int id = PreferencesManager.getInstance(this).getUserId();
        String loadUrl = Apis.CCAVENUE_URL+""+id;

        class MyJavaScriptInterface
		{
			@JavascriptInterface
		    public void processHTML(String html)
		    {	    	 
		    	if(html.indexOf("Sorry for inconvenience")!=-1){
		    		status = "Transaction Declined!";
		    		showToast(status);
		    	}else if(html.indexOf("Thank you for payments")!=-1){
		    		status = "Transaction Successful!";
		    		//new SavePackagesAsynctask(CCAvenueWebview.this,Integer.parseInt(activityID),userId,packageName,3000).execute();
		    	}else if(html.indexOf("Aborted")!=-1){
		    		status = "Transaction Cancelled!";
		    		showToast(status);
		    	}else{
		    		status = "Status Not Known!";
		    		showToast(status);
		    	}
		    }
		}
        
		final WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/20 Safari/537.31");
		webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		 
		webview.setWebViewClient(new WebViewClient(){
			@Override  
    	    public void onPageFinished(WebView view, String url) {
    	        super.onPageFinished(webview, url);
    	        if(url.indexOf("/ccavResponseHandler.aspx")!=-1){
    	        	webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
    	        }
    	    }  
    	    @Override
    	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    	        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
    	    }
		});
		
		 
		try {
			webview.loadUrl(loadUrl);
		} catch (Exception e) {
			showToast("Exception occured while opening webview.");
		}

    }
    
    public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}

    
}
