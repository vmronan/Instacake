package com.jsvr.instacake;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
import com.jsvr.instacake.data.Constants;

public class ViewVideosActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);
		
//		String projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
//		if(projectId.equals("")) {
//			showMyVideos();
//		}
//
//		showVideos(projectId);
		
		showMyVideos();
	}
	
	private void showMyVideos() {
		File directory = Constants.DIR_MY_THUMBS;
		String[] thumbnails = directory.list();
		for(String s : thumbnails) {
			Log.v("showMyVideos", "thumbnail: " + s);
		}
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
	}

//	private void showVideos(String projectId) {
//		// Look in Pictures/Instacake/Me for thumbnails
//		File directory = Constants.DIR_MY_THUMBS;
//		String[] thumbnails = directory.list();
//		ThumbnailListArrayAdapter adapter = new ThumbnailListArrayAdapter(this, R.layout.thumbnail_row, thumbnails);
//		ListView mFeed = (ListView) findViewById(R.id.listview_videos);
//		mFeed.setAdapter(adapter);
//	}
}
