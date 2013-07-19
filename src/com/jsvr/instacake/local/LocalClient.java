package com.jsvr.instacake.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class LocalClient {
		
	public static void createProject(String projectId, String title, String instaId) {
		// Create proj_123.json file for project
		Project project = new Project(projectId, title, instaId);
		JSONManager.saveNewProject(project);

		// Save new project filename to projects.txt
		try {
			File projectstxt = new File(Constants.getProjectsFilePath());
			if (!projectstxt.exists()){
					projectstxt.createNewFile();
			}
			BufferedWriter buf = new BufferedWriter(new FileWriter(projectstxt, true));		// "true" tells it to append to the existing file, not overwrite it
			buf.append(projectId);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addUserToProject(Context context, String instaId, String projectId) {
		JSONManager.addUserToProject(context, instaId, projectId);
	}
	
	public static void addVideoToProject(Context context, String videoId, String projectId) {
		JSONManager.addVideoToProject(context, videoId, projectId);
	}

}
