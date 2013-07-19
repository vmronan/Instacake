package com.jsvr.instacake;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.LocalClient;

public class ViewVideosActivity extends Activity {
	
	String projectId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		
		projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
		setupSpinner();
		if(projectId.equals("")) {
			Log.v("onCreate", "showing all my videos");
			showMyVideos();
		}
		else {
			Log.v("onCreate", "showing videos for project " + projectId);
//			showVideos(projectId);			
		}
	}
	
	private void setupSpinner() {
		// Get list of projects
		String[] projects = LocalClient.getProjectTitles();
		Spinner spinner = (Spinner) findViewById(R.id.projects_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, projects);
		spinner.setAdapter(adapter);
	}

	// Show all of my videos - gets all thumbnails from Pictures/Instacake/Me
	private void showMyVideos() {
		File directory = Constants.getMyThumbsDir();
		String[] thumbnails = directory.list();
		for(String s : thumbnails) {
			Log.v("showMyVideos", "thumbnail: " + s);
		}
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
	}
}
