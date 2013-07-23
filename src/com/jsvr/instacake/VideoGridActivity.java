package com.jsvr.instacake;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import com.jsvr.instacake.adapters.ImageAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;

public class VideoGridActivity extends Activity {
	
	boolean selectorOn;
	String projectUid;
	ArrayList<String> projectUids;
	SharedPreferences mPrefs;
	GridView mGridview;
	String[] mThumbs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_grid);
		mGridview = (GridView) findViewById(R.id.gridview);
		
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		projectUid = getIntent().getStringExtra(Constants.PROJECT_UID_KEY);
		selectorOn = false;
		
		projectUids =  LocalClient.getProjectUids();
		setupSpinner();
		buildGrid();
		
		SyncCallback refreshVideosOnUiThread = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject){
				String response = (String) responseObject;
				System.out.println("response is " + response);
				//TODO: track and implement statusCode properly
				if (statusCode == Sync.RESPONSE_OK){
					updateGrid();
				}
			}
		};
		
		String accessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, Constants.ERROR);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        
		Sync.updateMyMovies(accessToken, dm, refreshVideosOnUiThread);
	}

	protected void updateGrid() {
		projectUids = LocalClient.getProjectUids();
		((BaseAdapter) ((ImageAdapter) mGridview.getAdapter()).setThumbs(LocalClient.getMyThumbnailPaths())).notifyDataSetChanged();
		Log.v("updateGrid", "notifyDataSetChanged");
//		((BaseAdapter) mGridview.getAdapter()).notifyDataSetChanged();
	}

	private void setupSpinner() {
		// Get list of project titles
		String[] projects = LocalClient.getProjectTitles(projectUids);
		
		// Set the adapter
		Spinner spinner = (Spinner) findViewById(R.id.projects_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
										android.R.layout.simple_spinner_item, projects);
		spinner.setAdapter(adapter);
		
		// Set listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				projectUid = projectUids.get(position);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Nothing to do
			}
		});
		
		// Show the current project by default
		spinner.setSelection(projectUids.indexOf(projectUid));
	}

	private void buildGrid() {
		// Get thumbnail paths
		mThumbs = LocalClient.getMyThumbnailPaths();
		final String[] fthumbs = mThumbs;
		
		// Set the adapter

	    mGridview.setAdapter(new ImageAdapter(this, mThumbs));
		
	    // Set listener
		mGridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				if(selectorOn) {
					Sync.addVideoToProject(fthumbs[pos], 
										   Constants.getIdFromFilename(Uri.parse(fthumbs[pos]).getLastPathSegment()),
										   projectUid,
										   mPrefs.getString(Constants.USER_UID_KEY, Constants.ERROR));
				}
				else {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(Constants.getVideoPathFromThumbnailPath(fthumbs[pos])), "video/mp4");
					startActivity(intent);	
				}
			}
		});
		
	}
	
	public void toggleSelector(View v) {
		selectorOn = !selectorOn;
		if(selectorOn) {
			((Button)findViewById(R.id.select)).setTextColor(getResources().getColor(R.color.lime_green));	// green if selected
		}
		else {
			((Button)findViewById(R.id.select)).setTextColor(getResources().getColor(R.color.black));		// black otherwise
		}
	}
	
	// Go back to project
	public void done(View v) {
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_UID_KEY, projectUid);
		startActivity(i);
	}



}
