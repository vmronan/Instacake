package com.jsvr.instacake.gram;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.jsvr.instacake.R;
import com.jsvr.instacake.data.Constants;

public class TestGramClientActivity extends Activity {
	SharedPreferences mPrefs;
	String mInstaId;
	String mAccessToken;
	private long enqueue;
    private DownloadManager dm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_gram_client);
		
		getPrefs();
		initiateReceiver();
		
		
	}

	private void initiateReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                    long downloadId = intent.getLongExtra(
//                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Query query = new Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            TextView tv = (TextView) findViewById(R.id.textview);
                            tv.setText("Finished");
                        }
                    }
                }
            }
        };
        
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // TODO need to unregister receiver somewhere
		
	}

	private void getPrefs() {
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mInstaId = mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY");
		mAccessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, "NOTOKEN");
	}

	public void getMedia(View v){
		String mediaId = "3";
		GramClient.getMediaInfo("3", mAccessToken);
		
	}

    public void download(View v){
    	dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Request request = new Request(Uri.parse("http://distilleryvesper9-13.ak.instagram.com/090d06dad9cd11e2aa0912313817975d_101.mp4"));
        request.setDescription("Syncing with the Instagram servers");
        request.setTitle("Downloading Dat Vid");
        Uri uri = Uri.parse(Constants.getVideoFilePath("uniqueviduricheatingbrolol"));
        File file = new File(uri.getPath());
        System.out.println("File exists: " + file.exists());
        try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("File exists: " + file.exists());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "Instacake/Videos/myuniqueawesomedownload.mp4");
        enqueue = dm.enqueue(request);
    }
    
    public void viewDownloads(View view) {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }
}
