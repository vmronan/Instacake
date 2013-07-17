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

public class ViewProjectsActivity extends Activity {

	SharedPreferences mPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_projects);
		
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		
		populateProjects();
	}

	private void populateProjects() {
		ArrayList<String> batterList = getBatter();
	}
	
    public void newProject(View v) {
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	RailsClient.createProject(title, mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY"));
    }
    
    private ArrayList<String> getBatter() {
    	File projectFile = new File(Constants.PROJECTS_FILE_PATH);
    	ArrayList<String> batter = new ArrayList<String>();
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectFile);
			while(scanner.hasNext())
			{
				batter.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return batter;
    }
}
