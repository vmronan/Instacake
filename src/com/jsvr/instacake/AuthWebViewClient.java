package com.jsvr.instacake;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthWebViewClient extends WebViewClient {
	
	private Context mContext;
	
	public AuthWebViewClient(Context context) {
		mContext = context;
	}
	
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(Constants.CALLBACK_URL)) {
        	// Successful authorization. Request token present in url.
        	String requestToken = url.split("=")[1];
        	Log.v("shouldOverrideUrlLoading", "requestToken is " + requestToken);
        	new GetAccessTokenAsyncTask().execute(requestToken);
            return true;
        }
        
        return false;
    }
    
    public class GetAccessTokenAsyncTask extends AsyncTask<String, Void, Void> {
    	@Override
    	protected Void doInBackground(String... strings) {
    		String requestToken = strings[0];
			try {
				// Open the connection, set request method, and enable read and write.
				URL url = new URL(Constants.ACCESS_TOKEN_URL);
				HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				
				// Send the request
				OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
				osw.write(Constants.getAccessTokenRequestBody(requestToken));
				osw.flush();
				
				// Read the response and save the access token
				String response = readStream(urlConnection.getInputStream());
				System.out.println(response);
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				saveInstaId(jsonObject.getJSONObject("user").getString("id"));
			    saveAccessToken(jsonObject.getString("access_token")); 	
			} catch (Exception e) {
				e.printStackTrace();
			}
    		  		
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		mContext.startActivity(new Intent(mContext, MainActivity.class));
    	}
    }
    
    //ZOMGZSSEZZZY	
    private String readStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }
    
    public void saveInstaId(String instaId) {
		Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit();
    	editor.putString(Constants.INSTA_ID_KEY, instaId);
    	editor.commit();
    	Log.v("saveInstaId", "saved instaId " + instaId);
		
	}

	public void saveAccessToken(String accessToken) {
    	Editor editor = mContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).edit();
    	editor.putString(Constants.ACCESS_TOKEN_KEY, accessToken);
    	editor.commit();
    	Log.v("saveAccessToken", "saved access token " + accessToken);
    }
}
