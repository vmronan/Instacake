package com.jsvr.instacake;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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
	
	private ArrayList<String> getThumbnails() {
    	File projectFile = new File(Constants.getProjectFilePath(projectId));
    	ArrayList<String> idList = new ArrayList<String>();
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectFile);
			while(scanner.hasNext())
			{
				idList.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return idList;
    }
}
