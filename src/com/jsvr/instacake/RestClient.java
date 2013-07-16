package com.jsvr.instacake;

import java.io.File;
import java.io.FileNotFoundException;

import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {
	
	public static AsyncHttpResponseHandler getResponseHandler(String... responseHandler){
		final String tag = (responseHandler.length > 0) ? responseHandler[0] : "generic";
		return new AsyncHttpResponseHandler(){
			@Override
			public void onStart() {
				Log.v(tag + " response handler", "onStart()");
				super.onStart();
			}
			@Override
			public void onSuccess(String response) {
				Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
			}
			
			@Override
			public void onFailure(Throwable e, String response) {
				Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
				e.printStackTrace();
				super.onFailure(e, response);
			}
			
			@Override
			public void onFinish() {
				// Completed the request (either success or failure)
				Log.v(tag + " response handler", "onFinish()");
				super.onFinish();
			}
		};
		
	}

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      	client.post(url, params, responseHandler);
    }

    public static RequestParams buildParams(String[] keys, String[] values){
	    RequestParams params = new RequestParams();
	    int keysLength = keys.length;
	    for (int i = 0; i < keysLength; ++i){
	      params.put(keys[i], values[i]);
	    }
	    return params;
    }
    
}
