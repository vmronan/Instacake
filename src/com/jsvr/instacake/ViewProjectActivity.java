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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jsvr.instacake.adapters.ThumbnailListArrayAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.JSONManager;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;

public class ViewProjectActivity extends Activity {

	String projectId;
	SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		projectId = getIntent().getStringExtra(Constants.PROJECT_ID_KEY);
		this.setTitle(JSONManager.getProjectTitle(projectId));
		setContentView(R.layout.activity_view_project);
		
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        updateUsers();
        updateVideos();
        
//        showThumbnails();
	}
	
	public void addUser(View v) {
    	String newUser = ((EditText)findViewById(R.id.project_new_user)).getText().toString();
    	RailsClient.addUserToProject(newUser, projectId);
    	LocalClient.addUserToProject(newUser, projectId);
    	updateUsers();
	}
	
	// Shows the thumbnails for all videos in this project
	private void showThumbnails() {
		final String[] thumbnailUris = getThumbnailUris();
		
		ThumbnailListArrayAdapter adapter = new ThumbnailListArrayAdapter(this, R.layout.thumbnail_row, thumbnailUris);
		ListView listView = (ListView) findViewById(R.id.listview_thumbnails);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get URI of the clicked-on thumbnail, get the ID from the URI, and play the corresponding video
				String videoId = (thumbnailUris[position].split("_")[1]).split(".")[0];
				Uri videoUri = Uri.parse(Constants.getMoviesPath(videoId, true));
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setDataAndType(videoUri, "video/mp4");
				startActivity(i);
			}
		});
	}
	
	private String[] getThumbnailUris() {
    	ArrayList<String> idList = JSONManager.getVideoIds(projectId);
		ArrayList<String> uriList = new ArrayList<String>();
    	
    	for(String id : idList) {
    		uriList.add("IMG_" + id + ".bmp");		// TODO this will only show thumbnails in my thumbnail directory, not friends, because of the adapter
    	}
		// Convert arraylist to array of strings to we can use the image adapter later
		String[] uriArray = new String[uriList.size()];
		uriArray = uriList.toArray(uriArray);
		
		return uriArray;
    }
	
	private void updateUsers() {
	    ArrayList<String> users = JSONManager.getUsers(projectId);
	    String usersStr = "";
		if(users.size() > 0) {
			usersStr = users.get(0);
			users.remove(0);
			for(String user : users) {
				usersStr += ", " + user;
			}
		}
	    ((TextView)findViewById(R.id.users)).setText(usersStr);
	    ((EditText)findViewById(R.id.project_new_user)).setText("");
	}
	
	public void addVideos(View v) {
		Intent i = new Intent(this, ViewVideosActivity.class);
		i.putExtra(Constants.PROJECT_ID_KEY, projectId);
    	startActivity(i);
	}
	
	public void addVideo(View v) {
    	String newVid = ((EditText)findViewById(R.id.new_video)).getText().toString();
    	Log.v("addVideo", "adding video " + newVid);
    	LocalClient.addVideoToProject(newVid, projectId);
    	updateVideos();
	}
	
	private void updateVideos() {
		Log.v("updateVideos", "updating videos");
	    ArrayList<String> videos = JSONManager.getVideoIds(this, projectId);
	    String videosStr = "";
		if(videos.size() > 0) {
			videosStr = videos.get(0);
			videos.remove(0);
			for(String video : videos) {
				videosStr += ", " + video;
			}
		}
		else {
			videosStr = "This project does not have any videos yet :(";
		}
	    ((TextView)findViewById(R.id.videos)).setText(videosStr);
	    ((EditText)findViewById(R.id.new_video)).setText("");
	}
}
