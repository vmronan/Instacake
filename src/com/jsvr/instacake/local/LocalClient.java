package com.jsvr.instacake.local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.net.Uri;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;

public class LocalClient {

	public static void createProject(String projectUid, String title, String userUid, String username) {
		// Create proj_123.json file for project
		Project project = new Project(projectUid, title, userUid, username);
		LocalJSONManager.saveNewProject(project);

		// Save new project filename to projects.txt
		try {
			File projectstxt = new File(Constants.getProjectsFilePath());
			BufferedWriter buf = new BufferedWriter(new FileWriter(projectstxt, true));		// "true" tells it to append to the existing file, not overwrite it
			buf.append(projectUid);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addUserToProject(String userUid, String projectUid, String username) {
		LocalJSONManager.addUserToProject(userUid, projectUid, username);
	}
	
	public static void addVideoByThumbnailPath(String thumbnailPath, String projectUid) {
		LocalJSONManager.addVideoByThumbnailPath(thumbnailPath, projectUid);
	}
	
	public static String[] getProjectTitles(ArrayList<String> projectUids) {
		int numProjects = projectUids.size();
		String[] titles = new String[numProjects];
		for(int i = 0; i < numProjects; i++) {
			titles[i] = LocalJSONManager.getProjectTitle(projectUids.get(i));
		}
		
		return titles;
	}

	public static ArrayList<String> getProjectUids() {
		ArrayList<String> projectUids  = new ArrayList<String>();
		try {
			BufferedReader br;
			File projectsFile = new File(Constants.getProjectsFilePath());
			br = new BufferedReader(new FileReader(projectsFile));
			String line;
			while ((line = br.readLine()) != null) {
				projectUids.add(line.trim());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectUids;
	}
	
	// Turns ArrayList<String> into String[]
	public static String[] listToArray(ArrayList<String> arraylist) {
		int size = arraylist.size();
		String[] array = new String[size];
		for(int i = 0; i < size; i++) {
			array[i] = arraylist.get(i);
		}
		return array;
	}
	
	public static String[] getMyThumbnailPaths(){
		File directory = Constants.getMyThumbsDir();
		
		String[] thumbnails = directory.list();
		int numThumbs = thumbnails.length;
		for(int i = 0; i < numThumbs; i++) {
			thumbnails[i] = Constants.getMyThumbsDir().getPath() + File.separator + thumbnails[i];
		}
		return thumbnails;
	}

	// Parses list of thumbnail paths to get the UIDs for project's videos
	public static ArrayList<String> getVideoUidsForProject(String projectUid) {
		ArrayList<String> thumbPaths = LocalJSONManager.getThumbnailPaths(projectUid);
		ArrayList<String> uids = new ArrayList<String>();
		for (String thumbPath : thumbPaths){
			uids.add(Constants.getIdFromFilename(Uri.parse(thumbPath).getLastPathSegment()));
		}
		return uids;
	}

	public static ArrayList<String> getMyVideoUids() {
		String[] myVideoFilenames = Constants.getMyMoviesDir().list();
		ArrayList<String> myVideoUids = new ArrayList<String>();
		for (String videoFilename : myVideoFilenames){
			myVideoUids.add(Constants.getIdFromFilename(videoFilename));
		}
		return myVideoUids;
	}
	
	public static Project getProject(String projectUid) {
		return LocalJSONManager.getProject(projectUid);		
	}
	
	public static String getProjectTitle(String projectUid) {
		return LocalJSONManager.getProjectTitle(projectUid);		
	}
	
	public static ArrayList<String> getProjectUserUids(String projectUid) {
		return LocalJSONManager.getUserUids(projectUid);
	}
	
	public static ArrayList<String> getProjectUsernames(String projectUid) {
		return LocalJSONManager.getUsernames(projectUid);
	}
	
	public static String[] getProjectThumbnailPaths(String projectUid) {
		return listToArray(LocalJSONManager.getThumbnailPaths(projectUid));
	}
	
	public static void setProjectUserUidsList(String projectUid, ArrayList<String> userUids) {
		LocalJSONManager.setUserUids(projectUid, userUids);
	}
	
	public static void setProjectUsernamesList(String projectUid, ArrayList<String> usernames) {
		LocalJSONManager.setUsernames(projectUid, usernames);
	}
	
	public static void setProjectTitle(String projectUid, String title) {
		LocalJSONManager.setTitle(projectUid, title);
	}

	public static void saveProject(Project project) {
		// TODO Auto-generated method stub
		
	}
}
