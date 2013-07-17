package com.jsvr.instacake;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.jsvr.instacake.adapters.ThumbnailArrayAdapter;

public class ViewVideosActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_videos);

		showVideos();
	}

	private void showVideos() {
		// Look in Pictures/Instacake/Me for thumbnails
		File directory = Constants.DIR_MY_THUMBS;
		String[] thumbnails = directory.list();
		ThumbnailArrayAdapter adapter = new ThumbnailArrayAdapter(this, R.layout.thumbnail_row, thumbnails);
		ListView mFeed = (ListView) findViewById(R.id.listview_videos);
		mFeed.setAdapter(adapter);
	}
}
