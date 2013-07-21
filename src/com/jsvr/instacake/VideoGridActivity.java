package com.jsvr.instacake;

import java.util.ArrayList;

import android.app.Activity;
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
		selectorOn = false;
		
		projectUids =  LocalClient.readProjectsFile();
		setupSpinner();
		buildGrid();

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
				
			}
		});
		
		// Update the UI
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
		Log.v("toggleSelector", "selector is " + selectorOn);
		if(selectorOn) {
			((Button)findViewById(R.id.js_select)).setTextColor(getResources().getColor(R.color.lime_green));		// red if selected
		}
		else {
			((Button)findViewById(R.id.js_select)).setTextColor(getResources().getColor(R.color.black));		// black otherwise
		}
	}
	
	public void done(View v) {
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_UID_KEY, projectUid);
		startActivity(i);
	}



}
