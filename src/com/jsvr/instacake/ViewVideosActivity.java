package com.jsvr.instacake;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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

public class ViewVideosActivity extends Activity implements OnItemSelectedListener {
	
	String projectId;
	ArrayList<String> videoPaths;
	boolean selectorOn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		
		projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
		selectorOn = false;
		videoPaths = new ArrayList<String>();
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
	// Connect title with ID
	private void setupSpinner() {
		// Get list of projects
		String[] projects = LocalClient.getProjectTitles();
		Spinner spinner = (Spinner) findViewById(R.id.projects_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, projects);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		String project = parent.getItemAtPosition(pos).toString();
		Log.v("onItemSelected", "project: " + pos);
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
		// Nothing to do
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
		
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Log.v("onItemClick", "clicked on " + parent.getItemAtPosition(pos));
//				LocalClient.addVideoToProject(videoPath, projectId);
			}
		});
	}
	
	public void toggleSelector(View v) {
		selectorOn = !selectorOn;
		Log.v("toggleSelector", "selector is " + selectorOn);
		if(selectorOn) {
			((Button)findViewById(R.id.select)).setTextColor(0xff3300);		// red if selected
		}
		else {
			((Button)findViewById(R.id.select)).setTextColor(0x0);		// black otherwise
		}
	}
	
	public void done(View v) {
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_ID_KEY, projectId);
		startActivity(i);
	}
}