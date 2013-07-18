package com.jsvr.instacake.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class JSONManager {
	// The following functions all deal with a file like "proj_12345.json"
	
	public static void saveNewProject(Project project) {
		// Make proj_projectId.json
		saveProject(project);
	}

	// Get project object from JSON file with GSON
	public static Project getProject(Context context, String projectId) {
		Log.v("getProject", "reading from file " + Constants.getProjectPath(projectId));
		String json = readFromFile(context, new File(Constants.getProjectPath(projectId)));
		Type type = new TypeToken<Project>(){}.getType();
		return new Gson().fromJson(json, type);
	}
	
	// Saves project object to JSON file
	private static void saveProject(Project project) {
		File projFile = new File(Constants.getProjectPath(project.getProjectId()));
		String json = new Gson().toJson(project);
		writeToFile(projFile, json);
	}
	
	// Add user	to specific project
	public static void addUserToProject(Context context, String instaId, String projectId) {
		Project project = getProject(context, projectId);
		project.addUser(instaId);
		saveProject(project);
	}
	
	// Get project's users
	public static ArrayList<String> getUsers(Context context, String projectId) {
		return getProject(context, projectId).getUsers();
	}
	
	// Add video id or timestamp
	public void addVideoToProject(Context context, String videoId, String projectId) {
		Project project = getProject(context, projectId);
		project.addVideo(videoId);	// TODO is it video id or timestamp? deal with each case
		saveProject(project);
	}
	
	// Get video id or timestamp
	
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
	
	private static String readFromFile(Context context, File file) {
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
	
	// Get project title from project ID
	public static String getProjectTitle(Context context, String projectId) {
		Project project = getProject(context, projectId);
		String title = project.getTitle();
		return title;
	}
	
	// Get list of video IDs from project ID
	public static ArrayList<String> getVideoIds(Context context, String projectId) {
		Project project = getProject(context, projectId);
		ArrayList<String> videoIds = project.getVideoIds();
		return videoIds;
	}
	
}
