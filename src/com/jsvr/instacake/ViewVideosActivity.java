package com.jsvr.instacake;

import java.io.File;
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

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;

public class ViewVideosActivity extends Activity implements OnItemSelectedListener {
	
	String projectId;
	ArrayList<String> projectIds;
	boolean selectorOn;
	SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		
		projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
		selectorOn = false;
		projectIds = LocalClient.readProjectsFile();
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);      

		setupSpinner();
		if(projectId.equals("")) {
			Log.v("onCreate", "showing all my videos");
			showMyVideos();
		}
		else {
			Log.v("onCreate", "showing videos for project " + projectId);
			showVideos();
		}
	}
	
	// Connect title with ID
	private void setupSpinner() {
		// Get list of projects
		String[] projects = LocalClient.getProjectTitles();
		Spinner spinner = (Spinner) findViewById(R.id.projects_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, projects);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(projectIds.indexOf(projectId));
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		projectId = projectIds.get(position);
		Log.v("onItemSelected", "project: " + projectId + ", " + parent.getItemAtPosition(position).toString());
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
		// Nothing to do
	}

	
	private void showVideos() {
		File myDir = Constants.getMyThumbsDir();
		File friendsDir = Constants.getFriendsThumbsDir();
		String[] myThumbs = myDir.list();
		String[] friendsThumbs = friendsDir.list();
		int numMyThumbs = myThumbs.length;
		int numFriendsThumbs = friendsThumbs.length;
		String[] thumbnails = new String[numMyThumbs + numFriendsThumbs];
		for(int i = 0; i < numMyThumbs; i++) {
			thumbnails[i] = Constants.getMyThumbsDir().getPath() + File.separator + myThumbs[i];
		}
		for(int i = numMyThumbs; i < numMyThumbs+numFriendsThumbs; i++) {
			thumbnails[i] = Constants.getFriendsThumbsDir().getPath() + File.separator + friendsThumbs[i-numMyThumbs];
		}
		
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(selectorOn) {
					String videoPath = parent.getItemAtPosition(position).toString();
					LocalClient.addVideoToProject(videoPath, projectId);
					
					String instaId = mPrefs.getString(Constants.INSTA_ID_KEY, "");
					
					// Get video ID from video path
					String videoId = Constants.getIdFromFilename(Uri.parse(videoPath).getLastPathSegment());
					RailsClient.addVideoToProject(projectId, instaId, "", videoId);
					Log.v("onItemClick", "added " + videoPath + " to project " + projectId);
				}
			}
		});
		
	}
	
	
	// Show all of my videos - gets all thumbnails from Pictures/Instacake/Me
	private void showMyVideos() {
		File directory = Constants.getMyThumbsDir();
		String[] thumbnails = directory.list();
		int numThumbs = thumbnails.length;
		for(int i = 0; i < numThumbs; i++) {
			thumbnails[i] = Constants.getMyThumbsDir().getPath() + File.separator + thumbnails[i];
			Log.v("showMyVideos", "thumbnail: " + thumbnails[i]);
		}
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				if(selectorOn) {
					LocalClient.addVideoToProject(parent.getItemAtPosition(pos).toString(), projectId);
					Log.v("onItemClick", "added " + parent.getItemAtPosition(pos) + " to project " + projectId);
				}
			}
		});
	}
	
	public void toggleSelector(View v) {
		selectorOn = !selectorOn;
		Log.v("toggleSelector", "selector is " + selectorOn);
		if(selectorOn) {
			((Button)findViewById(R.id.select)).setTextColor(getResources().getColor(R.color.lime_green));		// red if selected
		}
		else {
			((Button)findViewById(R.id.select)).setTextColor(getResources().getColor(R.color.black));		// black otherwise
		}
	}
	
	public void done(View v) {
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_ID_KEY, projectId);
		startActivity(i);
	}
}