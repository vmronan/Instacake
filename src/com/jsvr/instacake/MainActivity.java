package com.jsvr.instacake;


import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.gram.TestGramClientActivity;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;
import com.jsvr.instacake.rails.TestRailsActivity;
import com.jsvr.instacake.sync.TestSyncActivity;

public class MainActivity extends Activity {
	SharedPreferences mPrefs;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.buildOrEnsureAllDirectories();
        
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);      
    }
    
    public void getFeed(View v) {
    	Intent i = new Intent(this, FeedActivity.class);
    	startActivity(i);
    }
    
    public void viewVideos(View v) {
    	Intent i = new Intent(this, ViewVideosActivity.class);
    	i.putExtra(Constants.PROJECT_ID_KEY, "");		// no project ID, so it will show all videos
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
    
    public void testRails(View v){
    	startActivity(new Intent(this, TestRailsActivity.class));
    }
    
    public void testGram(View v){
    	startActivity(new Intent(this, TestGramClientActivity.class));
    }
    public void testSync(View v){
    	startActivity(new Intent(this, TestSyncActivity.class));
    }
}
