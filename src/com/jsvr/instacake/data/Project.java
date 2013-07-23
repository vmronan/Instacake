package com.jsvr.instacake.data;

import java.util.ArrayList;

import android.util.Log;

import com.jsvr.instacake.local.LocalClient;

public class Project {
	public static final String NOT_A_PROJECT = "NOT A PROJECT";
	String mProjectUid;
	String mTitle;
	ArrayList<String> mUserUids;			// Instagram user id of each user in project
	ArrayList<String> mUsernames;			// Instagram username of each user in project
	ArrayList<String> mThumbnailPaths;		// local path of each video's thumbnail
	ArrayList<String> mVideoUids;			// id of each video in project
	
	// Constructor from when I create a new project in the app
	public Project(String projectUid, String title, String userUid, String username) {
		mProjectUid = projectUid;
		mTitle = title;
		mUserUids = new ArrayList<String>();
		mUsernames = new ArrayList<String>();
		mThumbnailPaths = new ArrayList<String>();
		mVideoUids = new ArrayList<String>();
		
		addUser(userUid, username);
	}

	// Constuctor for when we download a project from rails
	public Project(String projectUid,
			String title,
			ArrayList<String> userUids,
			ArrayList<String> usernames,
			ArrayList<String> videoUids) {
		mProjectUid = projectUid;
		mTitle = title;
		mUserUids = userUids;
		mUsernames = usernames;
		//TODO THIS IS HACKED AND BAD AND SHOULDN'T HAVE TO BE THIS WAY
		// If there is an old project, use its old thumbnail list
		if(LocalClient.getProject(projectUid).getThumbnailPaths() != null)
			mThumbnailPaths = LocalClient.getProject(projectUid).getThumbnailPaths();
		// Otherwise, create empty list of thumbnail paths
		else
			mThumbnailPaths = new ArrayList<String>();
		mVideoUids = videoUids;
	}


	public void addUser(String userUid, String username) {
		mUserUids.add(userUid);
		mUsernames.add(username);
	}
	
	public void addVideo(String videoUid, boolean isMine) {
		mVideoUids.add(videoUid);
		mThumbnailPaths.add(Constants.getThumbnailPath(videoUid, isMine));
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
	
	public ArrayList<String> getVideoUids() {
		return mVideoUids;
	}
	
	public void setUserUids(ArrayList<String> userUids) {
		mUserUids = userUids;
	}
	
	public void setUsernames(ArrayList<String> usernames) {
		mUsernames = usernames;
	}
}
