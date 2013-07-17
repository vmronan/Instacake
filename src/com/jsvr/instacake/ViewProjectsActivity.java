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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ViewProjectsActivity extends Activity {

	ArrayList<String> projectFiles;		// a project file is something like "proj_1234.txt"
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
		
		// Get title of each project, which is first line of proj_123.txt
		// TODO change how it gets title using json
		int numProjects = projectFiles.size();
		String titles[] = new String[numProjects];
		for(int i = 0; i < numProjects; i++) {
			Scanner scanner = new Scanner(projectFiles.get(i));
			if(scanner.hasNext()) {
				titles[i] = scanner.nextLine();
			}
		}
		
		// Display titles with ArrayAdapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_project_titles_row, titles);
		ListView listView = (ListView)findViewById(R.id.listview_projects);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get filename of the clicked-on project, get the ID from the filename, and open this project
				String projectId = (projectFiles.get(position).split("_")[1]).split(".")[0];
				Intent i = new Intent(mContext, ViewProjectActivity.class);
				i.putExtra(Constants.PROJECT_ID_KEY, projectId);
				startActivity(i);
			}
		});
	}
	
    public void newProject(View v) {
    	String title = ((EditText)findViewById(R.id.new_project_title)).getText().toString();
    	String instaId = mPrefs.getString(Constants.INSTA_ID_KEY, "NOKEY");
    	RailsClient.createProject(title, instaId);
    	LocalClient.createProject(title, instaId);
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
