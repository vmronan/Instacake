package com.jsvr.instacake;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.JSONManager;

public class ViewVideosActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		
		String projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
		if(projectId.equals("")) {
			Log.v("onCreate", "showing all my videos");
			showMyVideos();
		}
		else {
			Log.v("onCreate", "showing videos for project " + projectId);
			showVideos(projectId);			
		}
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

	// Shows all videos in a certain project - gets thumbnails from Pictures/Instacake/Me or Pictures/Instacake/Friends
	private void showVideos(String projectId) {
		ArrayList<String> ids = JSONManager.getVideoIds(this, projectId);
		for(String id : ids) {
			Log.v("showVideos", "video id: " + id);
		}
		ArrayList<String> filenames = getFilenames(ids);		// filenames of all videos in project
		for(String file : filenames) {
			Log.v("showVideos", "filename: " + file);
		}
		int numThumbs = filenames.size();
		Log.v("showVideos", "numThumbs: " + numThumbs);
		String[] thumbnails = new String[numThumbs];
		
		List<String> myVideos = Arrays.asList(Constants.getMyThumbsDir().list());				// filenames of thumbnails of all my videos
		List<String> friendsVideos = Arrays.asList(Constants.getFriendsThumbsDir().list());	// filenames of thumbnails of all my friends' videos
		Log.v("showVideos", "num my videos: " + myVideos.size());
		Log.v("showVideos", "num friends videos: " + friendsVideos.size());
		
		// Find each video in my or friends thumbnail directory and add its path to a list of thumbnails to display with the adapter
		for(int i = 0; i < numThumbs; i++) {
			String currentFile = filenames.get(i);
			Log.v("showVideo", "current file: " + currentFile);
			if(myVideos.contains(filenames.get(i))) {
				thumbnails[i] = getFilename(ids.get(i)); //Constants.getThumbnailPath(ids.get(i), true);
				Log.v("showVideos", "just added my thumbnail at path: " + thumbnails[i]);
			}
			else if(friendsVideos.contains(filenames.get(i))) {
				thumbnails[i] = getFilename(ids.get(i));
				Log.v("showVideos", "just added friend's thumbnail at path: " + thumbnails[i]);	// TODO change adapter to take in full path
			}
			else {
				Log.v("showVideos", "could not find file " + filenames.get(i));
			}
		}
		
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
	}
	
	private String getFilename(String id) {
		return "IMG_" + id + ".jpg";
	}
	
	private ArrayList<String> getFilenames(ArrayList<String> ids) {
		ArrayList<String> filenames = new ArrayList<String>();
		for(String id : ids) {
			filenames.add(getFilename(id));
		}
		return filenames;
	}
}
