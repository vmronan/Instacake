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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.jsvr.instacake.adapters.ProjectListAdapter;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;
import com.jsvr.instacake.local.JSONManager;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;

public class ViewProjectsActivity extends Activity {

	ArrayList<String> projectIds;
	SharedPreferences mPrefs;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_projects);
		
		projectIds = new ArrayList<String>();
		mPrefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
		mContext = this;
		
		showProjects();
	}

	private void showProjects() {
		getProjectIds();
		
		// Get each project
		int numProjects = projectIds.size();
		Project[] projects = new Project[numProjects];
		for(int i = 0; i < numProjects; i++) {
			projects[i] = JSONManager.getProject(this, projectIds.get(i));
		}
		
		// Display titles and users with ProjectListAdapter
		ProjectListAdapter adapter = new ProjectListAdapter(this, R.layout.listview_project_row, projects);
		ListView listView = (ListView)findViewById(R.id.listview_projects);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(mContext, ViewProjectActivity.class);
				i.putExtra(Constants.PROJECT_ID_KEY, projectIds.get(position));
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
    
    // Gets list of project IDs
    private void getProjectIds() {
    	File projectsFile = new File(Constants.PATH_PROJECTS);
    	
    	Scanner scanner;
		try {
			scanner = new Scanner(projectsFile);
			while(scanner.hasNext())
			{
				projectIds.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}
