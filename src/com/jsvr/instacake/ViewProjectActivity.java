package com.jsvr.instacake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
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
        
        showThumbnails();
	}
	
	public void addUser(View v) {
    	String newUser = ((EditText)findViewById(R.id.project_new_user)).getText().toString();
    	RailsClient.addUserToProject(newUser, projectId);
    	LocalClient.addUserToProject(newUser, projectId);
    	updateUsers();
	}
	
	private void showThumbnails() {
		String[] thumbnails = LocalClient.getArray(JSONManager.getVideoPaths(projectId));
		
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
	}
	
	
	
	// Shows all videos in a certain project - gets thumbnails from Pictures/Instacake/Me or Pictures/Instacake/Friends
	private void showVideos(String projectId) {
		Log.v("showVideos", "starting");
		ArrayList<String> ids = JSONManager.getVideoPaths(projectId);
		int numThumbs = 0;
		if(ids == null) {
			return;
		}
		else {
			numThumbs = ids.size();
		}
		ArrayList<String> filenames = getFilenames(ids);		// filenames of all videos in project
		
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
	
	
	
	
	// Shows the thumbnails for all videos in this project
//	private void showThumbnails() {
//		final String[] thumbnailUris = getThumbnailUris();
//		
//		ThumbnailListArrayAdapter adapter = new ThumbnailListArrayAdapter(this, R.layout.thumbnail_row, thumbnailUris);
//		ListView listView = (ListView) findViewById(R.id.listview_thumbnails);
//		listView.setAdapter(adapter);
//		
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// Get URI of the clicked-on thumbnail, get the ID from the URI, and play the corresponding video
//				String videoId = (thumbnailUris[position].split("_")[1]).split(".")[0];
//				Uri videoUri = Uri.parse(Constants.getMoviesPath(videoId, true));
//				Intent i = new Intent();
//				i.setAction(Intent.ACTION_VIEW);
//				i.setDataAndType(videoUri, "video/mp4");
//				startActivity(i);
//			}
//		});
//	}
//	
//	private String[] getThumbnailUris() {
//    	ArrayList<String> idList = JSONManager.getVideoIds(this, projectId);
//		ArrayList<String> uriList = new ArrayList<String>();
//    	
//    	for(String id : idList) {
//    		uriList.add("IMG_" + id + ".bmp");		// TODO this will only show thumbnails in my thumbnail directory, not friends, because of the adapter
//    	}
//		// Convert arraylist to array of strings to we can use the image adapter later
//		String[] uriArray = new String[uriList.size()];
//		uriArray = uriList.toArray(uriArray);
//		
//		return uriArray;
//    }
	
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
	
}
