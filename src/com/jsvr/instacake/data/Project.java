package com.jsvr.instacake.data;

import java.util.ArrayList;

public class Project {
	String mProjectId;
	String mTitle;
	ArrayList<String> mUsers;			// Instagram id of each user in project
	ArrayList<String> mVideoPaths;		// local path of each video
//	ArrayList<String> mVideoTimes;		// created_time of each video
	
	// Create new project with one user and no videos
	public Project(String projectId, String title, String instaId) {
		mProjectId = projectId;
		mTitle = title;
		mUsers = new ArrayList<String>();
		mVideoPaths = new ArrayList<String>();
//		mVideoTimes = new ArrayList<String>();
		
		addUser(instaId);
	}
	
	public void addUser(String instaId) {
		mUsers.add(instaId);
	}
	
	public void addVideo(String videoId) {
		mVideoPaths.add(videoId);
	}

	
	// Getters and setters for instance variables
	public String getProjectId() {
		return mProjectId;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public ArrayList<String> getUsers() {
		return mUsers;
	}
	
	public ArrayList<String> getVideoPaths() {
		return mVideoPaths;
	}
}
