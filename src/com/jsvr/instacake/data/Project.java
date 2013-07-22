package com.jsvr.instacake.data;

import java.util.ArrayList;

import android.util.Log;

public class Project {
	String mProjectUid;
	String mTitle;
	ArrayList<String> mUserUids;			// Instagram user id of each user in project
	ArrayList<String> mUsernames;			// Instagram username of each user in project
	ArrayList<String> mThumbnailPaths;		// local path of each video's thumbnail
//	ArrayList<String> mVideoTimes;			// created_time of each video
	
	// Create new project with one user and no videos
	public Project(String projectUid, String title, String userUid, String username) {
		mProjectUid = projectUid;
		mTitle = title;
		mUserUids = new ArrayList<String>();
		mUsernames = new ArrayList<String>();
		mThumbnailPaths = new ArrayList<String>();
//		mVideoTimes = new ArrayList<String>();
		
		addUser(userUid, username);
	}
	
	public void addUser(String userUid, String username) {
		mUserUids.add(userUid);
		mUsernames.add(username);
	}
	
	public void addVideo(String videoPath) {
		//TODO: honestly what the fuck. this is breaking everything
		if(mThumbnailPaths == null) {
			mThumbnailPaths = new ArrayList<String>();
		}
		
		mThumbnailPaths.add(videoPath);
		Log.v("addVideo", "adding " + videoPath);
	}

	
	// Getters and setters for instance variables
	public String getProjectUid() {
		return mProjectUid;
	}

	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public ArrayList<String> getUserUids() {
		return mUserUids;
	}
	
	public ArrayList<String> getUsernames() {
		return mUsernames;
	}
	
	public ArrayList<String> getThumbnailPaths() {
		return mThumbnailPaths;
	}

//	public ArrayList<String> getUsernames() {
//		ArrayList<String> usernames = new ArrayList<String>();
//		for (String user : mUsers){
//			usernames.add()
//		}
//	}
}
