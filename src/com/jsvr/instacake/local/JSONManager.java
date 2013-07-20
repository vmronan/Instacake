package com.jsvr.instacake.local;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class JSONManager {
	
	public static void saveNewProject(Project project) {
		// Make proj_projectId.json
		saveProject(project);
		Log.v("saveNewProject", "users: " + project.getUsers());
	}

	// Saves project object to JSON file
	private static void saveProject(Project project) {
		File projFile = new File(Constants.getProjectPath(project.getProjectId()));
		String json = new Gson().toJson(project);
		Log.v("saveProject", "users: " + project.getUsers());
		writeToFile(projFile, json);
	}
	
	// Add user	to specific project
	public static void addUserToProject(String instaId, String projectId) {
		Project project = getProject(projectId);
		project.addUser(instaId);
		saveProject(project);
	}
	
	public static void addVideoToProject(String videoPath, String projectId) {
		Project project = getProject(projectId);
		project.addVideo(videoPath);
		Log.v("addVideoToProject", "adding video " + videoPath);
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
	public static Project getProject(String projectId) {
		String json = readFromFile(new File(Constants.getProjectPath(projectId)));
		Type type = new TypeToken<Project>(){}.getType();
		return new Gson().fromJson(json, type);
	}

	// Get project's users
	public static ArrayList<String> getUsers(String projectId) {
		return getProject(projectId).getUsers();
	}
	
	// Get project title from project ID
	public static String getProjectTitle(String projectId) {
		Project project = getProject(projectId);
		String title = project.getTitle();
		return title;
	}
	
	// Get list of video paths from project ID
	public static ArrayList<String> getVideoPaths(String projectId) {
		Project project = getProject(projectId);
		ArrayList<String> videoIds = project.getVideoPaths();
		return videoIds;
	}
	
}
