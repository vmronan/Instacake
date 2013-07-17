package com.jsvr.instacake;

import android.util.Log;

public class LocalClient {
	
	private static String mInstaId;

	public static void createProject(String title, String instaId) {
		if (instaId.equals("NOKEY")){
			Log.v("createProject", "failed to find valid insta_id");
			return;
		}
		mInstaId = instaId;

		// Create proj_123.json file for project
		
		// Check if projects.txt exists. Create it if not
		// Add proj_123.json to projects.txt
	}

	public static void addUserToProject(String instaId, String projectId) {
		if (projectId.equals("0")){
			Log.v("addUserToProject", "project id is not valid");
			return;
		}
		
		// Add user to proj_123.json
	}
	
	public static void addVideoToProject(String projectId, String videoId) {
		// Add videoId to proj_123.json
	}
}
