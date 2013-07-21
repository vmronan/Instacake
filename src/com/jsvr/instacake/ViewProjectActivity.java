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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsvr.instacake.adapters.ThumbnailGridArrayAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.local.LocalJSONManager;
import com.jsvr.instacake.rails.RailsClient;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;

public class ViewProjectActivity extends Activity {

	String mProjectUid;
	SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProjectUid = getIntent().getStringExtra(Constants.PROJECT_UID_KEY);
		this.setTitle(LocalJSONManager.getProjectTitle(mProjectUid));
		setContentView(R.layout.activity_view_project);
		
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        updateUsers();
        
        showThumbnails();
	}
	
	public void addUser(View v) {
    	String newUsername = ((EditText)findViewById(R.id.project_new_user)).getText().toString();
    	
    	// After Sync asynchronously adds the user to the project, it will send a callback through the
    	// SyncCallback passed to addUserToProject
    	
    	// Set up listener
		SyncCallback updateUsersOnUiThread = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, String response) {
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
		final String[] thumbnails = LocalClient.getArray(LocalJSONManager.getVideoPaths(mProjectUid));
		
		ThumbnailGridArrayAdapter adapter = new ThumbnailGridArrayAdapter(this, R.layout.thumbnail_tile, thumbnails);
		GridView grid = (GridView) findViewById(R.id.gridview_videos);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(Constants.getVideoPathFromThumbnailPath(thumbnails[position])), "video/mp4");
				startActivity(intent);			
			}
		});
	}
	
	private void updateUsers() {
	    ArrayList<String> users = LocalJSONManager.getUsers(mProjectUid);
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
		i.putExtra(Constants.PROJECT_UID_KEY, mProjectUid);
    	startActivity(i);
	}
	
}
