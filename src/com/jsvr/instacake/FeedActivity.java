package com.jsvr.instacake;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jsvr.instacake.adapters.ThumbnailArrayAdapter;

public class FeedActivity extends Activity {
	
	private String[] mThumbnails = new String[]{};
	private ListView mFeed;
	private Context mContext;//CowboyUnderTheMoon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		
		mContext = this;
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        android.R.layout.simple_list_item_1, mThumbnails);
		mFeed = (ListView) findViewById(R.id.feed);
		mFeed.setAdapter(adapter);
		
		populateFeed();
	}

	private void populateFeed() {
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    	String accessToken = prefs.getString(Constants.ACCESS_TOKEN_KEY, "NOTOKEN");
    	if (!accessToken.equals("NOTOKEN")){
    		new GetFeedAsyncTask().execute(accessToken);
    	} else {
    		Log.v("populateFeed","failed to find access token in shared prefs");
    	}
	}

    
    public class GetFeedAsyncTask extends AsyncTask<String, Void, Void> {
    	@Override
    	protected Void doInBackground(String... strings) {
    		String accessToken = strings[0];
    		System.out.println("doInBackground");
			try {
				System.out.println("try");
				// Open the connection
				URL url = new URL(Constants.getFeedUrl(accessToken));
				HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);

				// Read and parse the response
				String response = readStream(urlConnection.getInputStream());
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				JSONArray dataArray = jsonObject.getJSONArray("data");
				
				// This is probably really bad, but we're
				int length = dataArray.length();
				int videoCount = 0;
				System.out.println("length is " + length);
				for (int i=0; i < length; ++i){
					JSONObject element = dataArray.getJSONObject(i);
					if (element.getString("type").equals("video")){
						videoCount++;
					}
				}
				
				// 
				mThumbnails = new String[videoCount];
				for (int i=0; i < length; ++i){
					JSONObject element = dataArray.getJSONObject(i);
					System.out.println("element is " + element.toString());
					if (element.getString("type").equals("video")){
						mThumbnails[i] = element.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
						Log.v("*", "just added " + mThumbnails[i]);
					}
				}
				
			} catch (Exception e) {
				System.out.println("error");
				e.printStackTrace();
			}
    		 System.out.println("done");
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		Log.v("onPostExecute" , "now setting the adapter with mThumbnails");
    		ThumbnailArrayAdapter adapter = new ThumbnailArrayAdapter(mContext, 
    		        R.layout.thumbnail_row, mThumbnails);
    		
    		mFeed.setAdapter(adapter);
    	}
    }
    
    private String readStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\A").next();
    }
	
}
