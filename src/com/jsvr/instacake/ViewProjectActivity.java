package com.jsvr.instacake;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsvr.instacake.adapters.ImageAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.local.LocalJSONManager;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;

public class ViewProjectActivity extends Activity {

	String mProjectUid;
	SharedPreferences mPrefs;
	private GridView mGridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		
		mProjectUid = getIntent().getStringExtra(Constants.PROJECT_UID_KEY);	
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        
        Log.v("onCreate", "PRJ_" + mProjectUid + ".json: " + LocalJSONManager.readFromFile(new File(Constants.getProjectPath(mProjectUid))));
        
        updateTitle();
        updateUsers();
        Log.v("onCreate", "showing thumbnails");
        showThumbnails();
		
		SyncCallback updateProjectOnUiThread = new SyncCallback() {
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				// TODO: track and handle status codes correctly
				if(statusCode == Sync.RESPONSE_OK) {
					Log.v("updateProjectOnUiThread", "about to update everything in the project");
					updateTitle();
					updateUsers();
					Log.v("onCreate", "updating thumbnails");
					updateThumbnails();
				}
			}
		};
		
		String accessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, Constants.ERROR);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		
        Log.v("onCreate", "about to sync project");
		Sync.syncProject(mProjectUid, accessToken, dm, updateProjectOnUiThread);

	}
	
	public void addUser(View v) {
		EditText editText = (EditText)findViewById(R.id.project_new_user);
    	String newUsername = editText.getText().toString();
	    editText.setText("");
    	
	    // After Sync asynchronously adds the user to the project, it will send a callback through the
    	// SyncCallback passed to addUserToProject
    	
    	// Set up listener
		SyncCallback updateUsersOnUiThread = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String response = (String) responseObject;
				if (statusCode == Sync.RESPONSE_OK){
					// A user has been successfully added, so we update the ui.
					updateUsers();
				} else if (statusCode == Sync.ERROR){
					// response contains error message
					Log.e("updateUsersOnUiThread", "Had statusCode == ERROR... response was " + response);
					showAddUserFailed();
				} else {
					// Really bad error...
				}
			}
		};

    	Sync.addUserToProject(newUsername, mPrefs.getString(Constants.ACCESS_TOKEN_KEY, Constants.ERROR), mProjectUid, updateUsersOnUiThread);
	}
	
	private void showAddUserFailed(){
		Toast.makeText(this, "Could not find user!", Toast.LENGTH_SHORT).show();
	}
	
	private void showThumbnails() {
		final String[] thumbnails = LocalClient.getProjectThumbnailPaths(mProjectUid);
		Log.v("showThumbnails", "number of thumbnails: " + thumbnails.length);
		
//		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
//		GridView grid = (GridView) findViewById(R.id.gridview_videos);
//		grid.setAdapter(adapter);
		
		mGridView = (GridView) findViewById(R.id.gridview_videos);
		mGridView.setAdapter(new ImageAdapter(this, thumbnails));
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(Constants.getVideoPathFromThumbnailPath(thumbnails[position])), "video/mp4");
				startActivity(intent);			
			}
		});
	}
	
	private void updateThumbnails(){
		Log.v("updateThumbnails", "number of thumbnails: " + LocalClient.getProjectThumbnailPaths(mProjectUid).length);

		mGridView = (GridView) findViewById(R.id.gridview_videos);
		((BaseAdapter) ((ImageAdapter) mGridView.getAdapter()).setThumbs(LocalClient.getProjectThumbnailPaths(mProjectUid))).notifyDataSetChanged();
	}

	private void updateTitle() {
		this.setTitle(LocalClient.getProjectTitle(mProjectUid));
	}
	
	// Updates TextView showing the list of users
	private void updateUsers() {
	    ArrayList<String> users = LocalClient.getProjectUsernames(mProjectUid);
	    String usersStr = "";
		if(users.size() > 0) {
			int numUsers = users.size();
			Log.v("updateUsers", "num users: " + numUsers);
			usersStr = users.get(0);
			for(int i = 1; i < numUsers; i++) {
				usersStr += ", " + users.get(i);
			}
		}
	    ((TextView)findViewById(R.id.users)).setText(usersStr);
	}
	
	public void addVideos(View v) {
		Intent i = new Intent(this, VideoGridActivity.class);
		i.putExtra(Constants.PROJECT_UID_KEY, mProjectUid);
    	startActivity(i);
	}
	
}