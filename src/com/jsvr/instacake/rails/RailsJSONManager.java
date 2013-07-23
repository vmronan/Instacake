package com.jsvr.instacake.rails;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.jsvr.instacake.data.Project;

public class RailsJSONManager {

	public static ArrayList<String> parseForProjectsList(String response) {
		// TODO Auto-generated method stub
		ArrayList<String> myProjects = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray projects = (JSONArray) new JSONTokener(jsonObject.getString("projects")).nextValue();
			
			int numProjects = projects.length();
			for (int i=0; i<numProjects; i++){
				String projectUid = projects.getJSONObject(i).getString("uid");
				Log.v("parseForProjectsList", "projectUid is " + projectUid);
				myProjects.add(projectUid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return myProjects;
	}
	
//	public static String getProjectIdFromResponse(String response) {
//		try {
//			JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
//			String projectId = ((JSONObject) new JSONTokener(jsonObject.getString("project")).nextValue()).getString("id");
//			return projectId;
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return "0";
//	}

	public static Project parseForProject(String response){
		return new Project(getProjectUidFromResponse(response),
						   getProjectTitleFromResponse(response),
						   getUserUidsForProjectFromResponse(response), 
						   getUsernamesForProjectFromResponse(response),
						   getVideosForProjectFromResponse(response));
	}

	private static String getProjectUidFromResponse(String response) {
		try {
			JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			String projectUid = ((JSONObject) new JSONTokener(jsonObject.getString("project")).nextValue()).getString("uid");
			return projectUid;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "0";
	}

	private static String getProjectTitleFromResponse(String response) {
		try {
			JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			String title = ((JSONObject) new JSONTokener(jsonObject.getString("project")).nextValue()).getString("title");
			return title;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "0";
	}

	private static ArrayList<String> getUserUidsForProjectFromResponse(String response) {
		ArrayList<String> userUids = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray users = (JSONArray) new JSONTokener(jsonObject.getString("users")).nextValue();
			int numUsers = users.length();
			for (int i = 0; i < numUsers; i++){
				String userUid = users.getJSONObject(i).getString("uid");
				System.out.println("userUid is " + userUid);
				userUids.add(userUid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userUids;
	}
	
	private static ArrayList<String> getUsernamesForProjectFromResponse(String response) {
		ArrayList<String> usernames = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray users = (JSONArray) new JSONTokener(jsonObject.getString("users")).nextValue();
			int numUsers = users.length();
			for (int i = 0; i < numUsers; i++){
				String username = users.getJSONObject(i).getString("username");
				System.out.println("username is " + username);
				usernames.add(username);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return usernames;
	}

	private static ArrayList<String> getVideosForProjectFromResponse(String response) {
		ArrayList<String> videoUids = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray videos = (JSONArray) new JSONTokener(jsonObject.getString("videos")).nextValue();
			int numVideos = videos.length();
			for (int i=0; i<numVideos; i++){
				String videoUid = videos.getJSONObject(i).getString("uid");
				videoUids.add(videoUid);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return videoUids;
	}

}