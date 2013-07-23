package com.jsvr.instacake.data;

import java.util.ArrayList;

public class Project {
	public static final String NOT_A_PROJECT = "NOT A PROJECT";
	String mProjectUid;
	String mTitle;
	ArrayList<String> mUserUids;			// Instagram user id of each user in project
	ArrayList<String> mUsernames;			// Instagram username of each user in project
	ArrayList<String> mThumbnailPaths;		// local path of each video's thumbnail
	ArrayList<String> mVideoUids;			// video uids
	
	// Create new project with one user and no videos
	public Project(String projectUid, String title, String userUid, String username) {
		mProjectUid = projectUid;
		mTitle = title;
		mUserUids = new ArrayList<String>();
		mUsernames = new ArrayList<String>();
		mThumbnailPaths = new ArrayList<String>();
		
		addUser(userUid, username);
	}
	
	public Project(String projectUid,
			String title,
			ArrayList<String> userUids,
			ArrayList<String> usernames,
			ArrayList<String> videoUids) {
		mProjectUid = projectUid;
		mTitle = title;
		mUserUids = userUids;
		mUsernames = usernames;
		mThumbnailPaths = new ArrayList<String>();
		mVideoUids = videoUids;
	}

	public void addUser(String userUid, String username) {
		mUserUids.add(userUid);
		mUsernames.add(username);
	}
	
	public void addVideoByThumbnailPath(String thumbnailPath) {
		if(mThumbnailPaths == null) {
			mThumbnailPaths = new ArrayList<String>();
		}
		
		mThumbnailPaths.add(thumbnailPath);
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
	
	public ArrayList<String> getVideoUids(){
		return mVideoUids;
	}
	
	public void setUserUids(ArrayList<String> userUids) {
		mUserUids = userUids;
	}
	
	public void setUsernames(ArrayList<String> usernames) {
		mUsernames = usernames;
	}

//	public ArrayList<String> getUsernames() {
//		ArrayList<String> usernames = new ArrayList<String>();
//		for (String user : mUsers){
//			usernames.add()
//		}
//	}
}
