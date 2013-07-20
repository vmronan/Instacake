package com.jsvr.instacake.sync;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.jsvr.instacake.R;
import com.jsvr.instacake.data.Constants;

public class TestSyncActivity extends Activity {
	DownloadManager dm;
	SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_sync);
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
	}

	public void syncProjects(View v){
		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Sync.syncProjectsFile(mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"), 
							  mPrefs.getString(Constants.ACCESS_TOKEN_KEY, "NOTOKEN"),
							  dm);
	}

}
