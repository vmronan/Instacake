package com.jsvr.instacake.gram;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jsvr.instacake.R;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.sync.VideoSync;

public class TestGramClientActivity extends Activity {
	SharedPreferences mPrefs;
	String mInstaId;
	String mAccessToken;
    private DownloadManager dm;
    Context mContext;
    BroadcastReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_gram_client);
		
		getPrefs();
		mContext = this;
	}

	private void getPrefs() {
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mInstaId = mPrefs.getString(Constants.USER_UID_KEY, "NOKEY");
		mAccessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, "NOTOKEN");
		System.out.println("access token is " + mAccessToken);
	}
	
	private void initializeReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                	//TODO "you may not show a dialog or bind to a service from within a BroadcastReceiver"
                	// from http://developer.android.com/reference/android/content/BroadcastReceiver.html#ReceiverLifecycle
                	Toast.makeText(mContext, "HOORAY!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
	}

	public void downloadThumbnail(View v){
		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		GramClient.getThumbnail("501532579250690997_467380373", mAccessToken, dm, true);
		
	}


    public void downloadVideo(View v){
    	dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    	GramClient.getMovie("501532579250690997_467380373", mAccessToken, dm, true);
    }
    
    public void downloadBoth(View v){
    	dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    	GramClient.syncMovie("501532579250690997_467380373", mAccessToken, dm, true);
    }
    
    public void syncWithMyFeed(View v){
    	dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    	VideoSync.syncWithMyFeed(mAccessToken, dm);
    }
    
    public void viewDownloads(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }
    
    @Override
    public void onPause(){
    	if (receiver != null){
    		unregisterReceiver(receiver);
    	}
    	super.onPause();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	initializeReceiver();
    }
}
