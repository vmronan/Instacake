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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_grid);
		
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		projectUid = getIntent().getStringExtra(Constants.PROJECT_UID_KEY);
		selectorOn = false;
		
		projectUids =  LocalClient.readProjectsFile();
		setupSpinner();
		buildGrid();
		
		SyncCallback refreshVideosOnUiThread = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, String response){
				System.out.println("response is " + response);
				//TODO: track and implement statusCode properly
				if (statusCode == Sync.RESPONSE_OK){
					System.out.println("response is " + response);
					buildGrid();
				}
			}
		};
		
		String accessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, Constants.ERROR);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        
		Sync.updateMyMovies(accessToken, dm, refreshVideosOnUiThread);
	}

	private void setupSpinner() {
		// Get list of projects
		String[] projects = LocalClient.getProjectTitles();
		
		// Set the adapter
		Spinner spinner = (Spinner) findViewById(R.id.js_projects_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, projects);
		spinner.setAdapter(adapter);
		
		// Set listener
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				projectUid = projectUids.get(position);
				Log.v("onItemSelected", "project: " + projectUid + ", " + parent.getItemAtPosition(position).toString());
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
		final String[] thumbs = LocalClient.getMyThumbnailPaths();
		
		// Set the adapter
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this, thumbs));
		
	    // Set listener
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				if(selectorOn) {
					Sync.addVideoToProject(thumbs[pos], 
										   Constants.getIdFromFilename(Uri.parse(thumbs[pos]).getLastPathSegment()),
										   projectUid,
										   mPrefs.getString(Constants.USER_UID_KEY, Constants.ERROR));
					Log.v("onItemClick", "added " + Constants.getVideoPathFromThumbnailPath(thumbs[pos]) + " to project " + projectUid);
				}
				else {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(Constants.getVideoPathFromThumbnailPath(thumbs[pos])), "video/mp4");
					startActivity(intent);	
				}
			}
		});
		
	}
	
	public void toggleSelector(View v) {
		selectorOn = !selectorOn;
		if(selectorOn) {
			((Button)findViewById(R.id.js_select)).setTextColor(getResources().getColor(R.color.lime_green));	// green if selected
		}
		else {
			((Button)findViewById(R.id.js_select)).setTextColor(getResources().getColor(R.color.black));		// black otherwise
		}
	}
	
	// Go back to project
	public void done(View v) {
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_UID_KEY, projectUid);
		startActivity(i);
	}



}
