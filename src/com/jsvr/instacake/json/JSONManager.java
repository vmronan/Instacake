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
import com.jsvr.instacake.Constants;
import com.jsvr.instacake.Project;

public class JSONManager {
	// The following functions all deal with a file like proj_123.json
	
	public static void saveNewProject(Project project) {
		// Make proj_projectId.json
		File projFile = new File(Constants.DIR_PROJECTS.getPath() + File.separator + "proj_" + project.getProjectId() + ".json");
		String json = new Gson().toJson(project);
		writeToFile(projFile, json);
	}
	
	// Get project object from JSON file with GSON
	public static Project getProject(Context context, String projectId) {
		String json = readFromFile(context, new File(Constants.getProjectPath(projectId)));
		Log.v("getProject", "json is " + json);
		
		Type type = new TypeToken<Project>(){}.getType();
		return new Gson().fromJson(json, type);
	}
	
	// Add user	
	public void addUserToProject(String instaId, String projectId) {
		
	}
	
	// Get all users 
	// Add video id or timestamp
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
			Log.v("readFromFile", "about to read from file " + file.getPath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
//			FileInputStream in = context.openFileInput(file);
//		    InputStreamReader inputStreamReader = new InputStreamReader(in);
//		    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		    StringBuilder sb = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        sb.append(line);
		        Log.v("readFromFile", "line is " + line);
		    }
		    reader.close();
		    return sb.toString();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return null;
	}
	
	// Get project title from project filename (like "proj_123.json")
	public static String getProjectTitle(Context context, String projectFilename) {
		Log.v("getProjectTitle", "projectfilename is " + projectFilename);
		Project project = getProject(context, Constants.getIdFromFilename(projectFilename));
		String title = project.getTitle();
		Log.v("getProjectTitle", "title is " + title);
		return title;
	}
	
	// Get list of video IDs from project filename (like "proj_123.json")
	public static ArrayList<String> getVideoIds(Context context, String projectId) {
		Project project = getProject(context, projectId);
		ArrayList<String> videoIds = project.getVideoIds();
		return videoIds;
	}
	
}
