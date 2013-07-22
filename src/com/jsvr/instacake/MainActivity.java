package com.jsvr.instacake;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.rails.RailsClient;

public class MainActivity extends Activity {
	SharedPreferences mPrefs;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.buildOrEnsureAllDirectories();
        
        mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        
        loginIfNecessary();
    }
    
    public void loginIfNecessary(){
    	String accessToken = mPrefs.getString(Constants.ACCESS_TOKEN_KEY, "");
        if(accessToken.equals("")) {
        	Intent i = new Intent(this, LoginActivity.class);
        	startActivity(i);
        }
        
        if (Constants.DEVELOPMENT_MODE){
        	RailsClient.createUser(mPrefs.getString(Constants.USER_UID_KEY, Constants.ERROR), 
    				mPrefs.getString(Constants.USERNAME_KEY, Constants.ERROR));
        }
        
    }
    
    public void viewVideos(View v) {
    	Intent i = new Intent(this, ViewVideosActivity.class);
    	i.putExtra(Constants.PROJECT_UID_KEY, "");		// no project ID, so it will show all videos
    	startActivity(i);
    }
    
    public void viewProjects(View v) {
    	Intent i = new Intent(this, ViewProjectsActivity.class);
    	startActivity(i);
    }
    
    public void grid(View v){
    	startActivity(new Intent(this, VideoGridActivity.class));
    }
    
//    public void testRails(View v){
//    	startActivity(new Intent(this, TestRailsActivity.class));
//    }
//    
//    public void testGram(View v){
//    	startActivity(new Intent(this, TestGramClientActivity.class));
//    }
//    public void testSync(View v){
//    	startActivity(new Intent(this, TestSyncActivity.class));
//    }
}