package com.jsvr.instacake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class TestRailsActivity extends Activity {
	SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_rails);
		
		 mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
	}

    public void signIn(View v) {
    	Intent i = new Intent(this, LoginActivity.class);
    	startActivity(i);
    }
    
    
	public void createUser(View v){
		RailsClient.createUser(mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
	}
	
	public void createOtherUser(View v){
		RailsClient.createUser("somebogusvalue");
	}
	
	public void createProject(View v){
		RailsClient.createProject("My super cool project", mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
	}
	
	public void addVideoToProject(View v){
		String project_id = "1";
		String insta_user_id = mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY");
		String created_at = "12312312312";
		String insta_video_id = "324234234";
		RailsClient.addVideoToProject(project_id, insta_user_id, created_at, insta_video_id);
	}
	
	public void addUserToProject(View v){
		RailsClient.addUserToProject("somebogusvalue", "1");
	}
	
	public void addVideoToProjectAsOtherUser(View v){
		RailsClient.addVideoToProject("1", "somebogusvalue", "12312312", "8768768"); 
	}
	
	public void getProjectList(View v){
		RailsClient.getProjectsList(mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
	}
	
	public void getVideosForProject(View v){
		RailsClient.getVideosForProject("1");
	}
	

}
