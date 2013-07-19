package com.jsvr.instacake.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
	
	// Returns array of thumbnail paths
//	public static String[] getProjectThumbnails(String projectId) {
//		
//	}
	
	public static String[] getProjectTitles() {
		// Get arraylist of project IDs from projects.txt
		ArrayList<String> projectIds = new ArrayList<String>();
    	File projectsFile = new File(Constants.getProjectsFilePath());
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
		
		// Get each project
		int numProjects = projectIds.size();
		String[] titles = new String[numProjects];
		for(int i = 0; i < numProjects; i++) {
			titles[i] = JSONManager.getProjectTitle(projectIds.get(i));
		}
		
		return titles;
	}
}
