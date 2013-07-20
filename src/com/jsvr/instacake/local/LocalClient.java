package com.jsvr.instacake.local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

	public static void addUserToProject(String instaId, String projectId) {
		JSONManager.addUserToProject(instaId, projectId);
	}
	
	public static void addVideoToProject(String videoPath, String projectId) {
		JSONManager.addVideoToProject(videoPath, projectId);
	}

	public static ArrayList<String> readProjectsFile() {
		ArrayList<String> projectIds  = new ArrayList<String>();
		try {
			BufferedReader br;
			File projectsFile = new File(Constants.getProjectsFilePath());
			br = new BufferedReader(new FileReader(projectsFile));
			String line;
			while ((line = br.readLine()) != null) {
				projectIds.add(line.trim());
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectIds;
	}
	

}
