package com.jsvr.instacake;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.jsvr.instacake.adapters.ThumbnailArrayAdapter;

public class ViewProjectActivity extends Activity {

	String projectId;
	SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		
		projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        
        showThumbnails();
	}
	
	public void addUser(View v) {
    	String newUser = ((EditText)findViewById(R.id.project_new_user)).getText().toString();
    	RailsClient.addUserToProject(newUser, projectId);
	}
	
	// Shows the thumbnails for all videos in this project
	private void showThumbnails() {
		final String[] thumbnailUris = getThumbnailUris();
		
		ThumbnailArrayAdapter adapter = new ThumbnailArrayAdapter(this, R.layout.thumbnail_row, thumbnailUris);
		ListView listView = (ListView) findViewById(R.id.listview_thumbnails);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get URI of the clicked-on thumbnail, get the ID from the URI, and play the corresponding video
				String videoId = (thumbnailUris[position].split("_")[1]).split(".")[0];
				Uri videoUri = Uri.parse(Constants.getVideoFilePath(videoId));
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setDataAndType(videoUri, "video/mp4");
				startActivity(i);
			}
		});
	}
	
	private String[] getThumbnailUris() {
    	File projectFile = new File(Constants.getProjectFilePath(projectId));
    	ArrayList<String> uriList = new ArrayList<String>();
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectFile);
			while(scanner.hasNext())
			{
				uriList.add(Constants.getThumbnailFilePath(scanner.nextLine()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Convert arraylist to array of strings to we can use the image adapter later
		String[] uriArray = new String[uriList.size()];
		uriArray = uriList.toArray(uriArray);
		
		return uriArray;
    }
}
