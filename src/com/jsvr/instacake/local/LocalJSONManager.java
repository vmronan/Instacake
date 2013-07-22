package com.jsvr.instacake.local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class LocalJSONManager {
	
	protected static void saveNewProject(Project project) {
		saveProject(project);
	}

	// Saves project object to JSON file
	private static void saveProject(Project project) {
		File projFile = new File(Constants.getProjectPath(project.getProjectUid()));
		String json = new Gson().toJson(project);
		writeToFile(projFile, json);
	}
	
	// Add user	to specific project
	protected static void addUserToProject(String userUid, String projectUid, String username) {
		Project project = getProject(projectUid);
		project.addUser(userUid, username);
		saveProject(project);
	}
	
	protected static void addVideoByThumbnailPath(String thumbnailPath, String projectUid) {
		Project project = getProject(projectUid);
		project.addVideoByThumbnailPath(thumbnailPath);
		saveProject(project);
	}

	private static void writeToFile(File file, String data) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(data);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String readFromFile(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
		    StringBuilder sb = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		    }
		    reader.close();
		    return sb.toString();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return null;
	}

	// Get project object from JSON file with GSON
	protected static Project getProject(String projectUid) {
		File projectFile = new File(Constants.getProjectPath(projectUid));
		String json = ""; 
		if (!projectFile.exists()){
			// This project does not exist... we must create it.
			saveNewProject(new Project(projectUid, "temporary title", "temporary userUid", "temporary username"));
			json = readFromFile(projectFile);
			// Save new project filename to projects.txt
			try {
				File projectstxt = new File(Constants.getProjectsFilePath());
				BufferedWriter buf = new BufferedWriter(new FileWriter(projectstxt, true));		// "true" tells it to append to the existing file, not overwrite it
				buf.append(projectUid);
				buf.newLine();
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			json = readFromFile(projectFile);
		}
		Type type = new TypeToken<Project>(){}.getType();
		return new Gson().fromJson(json, type);
	}

	// Get project's user's IDs
	protected static ArrayList<String> getUserUids(String projectId) {
		return getProject(projectId).getUserUids();
	}
	
	// Get project's users' usernames
	public static ArrayList<String> getUsernames(String projectId) {
		return getProject(projectId).getUsernames();
	}
	
	// Get project title from project ID
	protected static String getProjectTitle(String projectUid) {
		Project project = getProject(projectUid);
		String title = project.getTitle();
		return title;
	}
	
	// Get list of thumbnail paths from project ID
	protected static ArrayList<String> getThumbnailPaths(String projectUid) {
		Project project = getProject(projectUid);
		return project.getThumbnailPaths();
	}
}
