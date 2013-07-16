package com.jsvr.instacake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    
    public void getFeed(View v){
    	Intent i = new Intent(this, FeedActivity.class);
    	startActivity(i);
    }
    
    public void newProject(View v){
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	RailsClient.createProject(title, mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
    }
    
    
}
