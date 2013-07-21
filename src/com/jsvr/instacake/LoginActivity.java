package com.jsvr.instacake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.gram.AuthWebViewClient;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		
		authorize();
	}
	
	public void authorize(){
		WebView loginWebView = (WebView) findViewById(R.id.login_webview);
		loginWebView.setVisibility(View.VISIBLE);
		loginWebView.setWebViewClient(new AuthWebViewClient(this));
		loginWebView.loadUrl(Constants.AUTH_URL_WITH_PARAMS);
	}
}
