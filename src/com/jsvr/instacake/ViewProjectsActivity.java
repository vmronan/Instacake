package com.jsvr.instacake;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.jsvr.instacake.adapters.ProjectListAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;
import com.jsvr.instacake.local.LocalJSONManager;
import com.jsvr.instacake.sync.Sync;

public class ViewProjectsActivity extends Activity {

	ArrayList<String> projectUids;
	SharedPreferences mPrefs;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_projects);
		
		projectUids = new ArrayList<String>();
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mContext = this;
		
		showProjects();
	}

	private void showProjects() {
		getProjectUids();
		
		// Get each project
		int numProjects = projectUids.size();
		Project[] projects = new Project[numProjects];
		for(int i = 0; i < numProjects; i++) {
			projects[i] = LocalJSONManager.getProject(projectUids.get(i));
		}
		
		// Display titles and users with ProjectListAdapter
		ProjectListAdapter adapter = new ProjectListAdapter(this, R.layout.listview_project_row, projects);
		ListView listView = (ListView)findViewById(R.id.listview_projects);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(mContext, ViewProjectActivity.class);
				i.putExtra(Constants.PROJECT_UID_KEY, projectUids.get(position));
				startActivity(i);
			}
		});
	}
	
    public void newProject(View v) {
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	String userUid = mPrefs.getString(Constants.USER_UID_KEY, Constants.ERROR);
    	String username = mPrefs.getString(Constants.USERNAME_KEY, Constants.ERROR);
    	
    	// Use Sync to create project with LocalClient and RailsClient
    	String projectUid = Sync.createProject(title, userUid, username);

    	// Show the new project
		Intent i = new Intent(this, ViewProjectActivity.class);
		i.putExtra(Constants.PROJECT_UID_KEY, projectUid);
		startActivity(i);
    }
    
    // Gets list of project UIDs
    private void getProjectUids() {
    	File projectsFile = new File(Constants.getProjectsFilePath());
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectsFile);
			while(scanner.hasNext())
			{
				projectUids.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}
