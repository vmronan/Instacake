package com.jsvr.instacake.rails;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RailsJSONManager {

	public static ArrayList<String> getProjectListFromResponse(String response) {
		// TODO Auto-generated method stub
		ArrayList<String> myProjects = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray projects = (JSONArray) new JSONTokener(jsonObject.getString("projects")).nextValue();
			
			int numProjects = projects.length();
			for (int i=0; i<numProjects; i++){
				String projectId = projects.getJSONObject(i).getString("id");
				System.out.println("projectId is " + projectId);
				myProjects.add(projectId);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return myProjects;
	}

	public static ArrayList<String> getVideosForProjectFromResponse(String response) {
		ArrayList<String> videoIds = new ArrayList<String>();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray videos = (JSONArray) new JSONTokener(jsonObject.getString("videos")).nextValue();
			int numVideos = videos.length();
			for (int i=0; i<numVideos; i++){
				String videoId = videos.getJSONObject(i).getString("insta_id");
				System.out.println("videoId is " + videoId);
				videoIds.add(videoId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return videoIds;
	}

}
