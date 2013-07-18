package com.jsvr.instacake;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

import com.jsvr.instacake.json.JSONManager;

public class LocalClient {
		
	public static void createProject(String projectId, String title, String instaId) {
		// Create proj_123.json file for project
		Project project = new Project(projectId, title, instaId);
		JSONManager.saveNewProject(project);

		// Save new project filename to projects.txt
		try {
			File projectstxt = new File(Constants.PATH_PROJECTS);
			if (!projectstxt.exists()){
					projectstxt.createNewFile();
			}
			BufferedWriter buf = new BufferedWriter(new FileWriter(projectstxt, true));		// "true" tells it to append to the existing file, not overwrite it
			Log.v("createProject", "appending " + Constants.getProjectFilename(projectId));
			buf.append(Constants.getProjectFilename(projectId));
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addUserToProject(String instaId, String projectId) {
		if (projectId.equals("0")){
			Log.v("addUserToProject", "project id is not valid");
			return;
		}
		
		// Add user to proj_123.json
	}
	
	public static void addVideoToProject(String projectId, String videoId) {
		// Add videoId to proj_123.json
	}
}