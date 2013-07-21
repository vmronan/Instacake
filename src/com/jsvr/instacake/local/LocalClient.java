package com.jsvr.instacake.local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class LocalClient {
		
	public static void createProject(String projectId, String title, String userUid) {
		// Create proj_123.json file for project
		Project project = new Project(projectId, title, userUid);
		LocalJSONManager.saveNewProject(project);

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
		LocalJSONManager.addUserToProject(instaId, projectId);
	}
	
	public static void addVideoToProject(String videoPath, String projectUid) {
		LocalJSONManager.addVideoToProject(videoPath, projectUid);
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
			titles[i] = LocalJSONManager.getProjectTitle(projectIds.get(i));
		}
		
		return titles;
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
			e.printStackTrace();
		}
		return projectIds;
	}
	
	// Turns ArrayList<String> into String[]
	public static String[] getArray(ArrayList<String> arraylist) {
		int size = arraylist.size();
		String[] array = new String[size];
		for(int i = 0; i < size; i++) {
			array[i] = arraylist.get(i);
		}
		return array;
	}
	
	public static String[] getMyThumbnailPaths(){
		File directory = Constants.getMyThumbsDir();
		String[] thumbnails = directory.list();
		int numThumbs = thumbnails.length;
		for(int i = 0; i < numThumbs; i++) {
			thumbnails[i] = Constants.getMyThumbsDir().getPath() + File.separator + thumbnails[i];
			Log.v("showMyVideos", "thumbnail: " + thumbnails[i]);
		}
		return thumbnails;
	}
}
