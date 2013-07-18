package com.jsvr.instacake;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jsvr.instacake.json.JSONManager;

public class ViewProjectsActivity extends Activity {

	ArrayList<String> projectFiles;		// a project file is something like "proj_1234.json"
	SharedPreferences mPrefs;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_projects);
		
		projectFiles = new ArrayList<String>();
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mContext = this;
		
		showProjects();
	}

	private void showProjects() {
		getProjectFiles();
		
		// Get title of each project
		int numProjects = projectFiles.size();
		String[] titles = new String[numProjects];
		for(int i = 0; i < numProjects; i++) {
			titles[i] = JSONManager.getProjectTitle(this, projectFiles.get(i));
			Log.v("showProjects", "next title is " + titles[i]);
		}
		
		// Display titles with ArrayAdapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
		ListView listView = (ListView)findViewById(R.id.listview_projects);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get filename of the clicked-on project, get the ID from the filename, and open this project
				String projectId = Constants.getIdFromFilename(projectFiles.get(position));
				Intent i = new Intent(mContext, ViewProjectActivity.class);
				i.putExtra(Constants.PROJECT_ID_KEY, projectId);
				startActivity(i);
			}
		});
	}
	
    public void newProject(View v) {
    	String projectId = String.valueOf((new Random()).nextInt());
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	String instaId = mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY");
    	RailsClient.createProject(title, instaId);
    	LocalClient.createProject(projectId, title, instaId);
    	
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_ID_KEY, projectId);
		startActivity(i);
    }
    
    private void getProjectFiles() {
    	File projectsFile = new File(Constants.PATH_PROJECTS);
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectsFile);
			while(scanner.hasNext())
			{
				projectFiles.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}
