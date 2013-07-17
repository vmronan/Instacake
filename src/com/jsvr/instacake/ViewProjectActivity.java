package com.jsvr.instacake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
		
	}
}
