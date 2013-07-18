package com.jsvr.instacake;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void signIn(View v) {
    	Intent i = new Intent(this, LoginActivity.class);
    	startActivity(i);
    }
    
    public void getFeed(View v) {
    	Intent i = new Intent(this, FeedActivity.class);
    	startActivity(i);
    }
    
    public void viewVideos(View v) {
    	Intent i = new Intent(this, ViewVideosActivity.class);
    	startActivity(i);
    }
    
    public void getProjects(View v) {
    	Intent i = new Intent(this, ViewProjectsActivity.class);
    	startActivity(i);
    }
    
    public void newProject(View v) {
    	String projectId = String.valueOf(Math.abs((new Random()).nextInt()));
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	String instaId = mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY");
    	RailsClient.createProject(title, instaId);
    	LocalClient.createProject(projectId, title, instaId);
    	
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_ID_KEY, projectId);
		startActivity(i);
    }
    
    public void createUser(View v){
    	RailsClient.createUser(mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
    }
    
    public void updateThumbs(View v) {
    	//  Add Instagram video thumbnails to DIR_MY_THUMBS if they're missing
    }
}
