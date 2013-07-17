package com.jsvr.instacake;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class Constants {
	
	public static final String PREFS_NAME = "com.jsvr.instacake.SHARED_PREFS";

	public static final String CLIENT_ID = "2441a48392cf4ab6a7343038f858ae15";
	public static final String CLIENT_SECRET = "781c8fe7656b4e03b8bf45a0990a1331";
	
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	public static final String ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String API_URL =  "https://api.instagram.com/v1";
	public static String CALLBACK_URL = "http://www.vanessaronan.com";
	
	public static final String AUTH_URL_WITH_PARAMS = AUTH_URL + "?client_id=" + CLIENT_ID +
			"&redirect_uri=" + CALLBACK_URL + "&response_type=code";
	
	public static final String ACCESS_TOKEN_KEY = "yodawgi'mtheaccesstokenkey";
	public static final String INSTA_ID_KEY = "yodawgi'mliketheinstagramidyouget";
	public static final String PROJECT_ID_KEY = "yodawgi'maprojectid";
	
	// My projects - Movies/Instacake/Projects directory
	public static File DIR_PROJECTS = getProjectsDir();
	public static String PATH_PROJECTS = getProjectsPath();		// path of projects.txt
	
	// My vides - Movies/Instagram directory
	public static File DIR_MY_VIDEOS = getMyVideosDir();
//	public static String PATH_MY_VIDEOS = getMyVideosPath();
	
	// My friends' videos - Movies/Instacake directory
	public static File DIR_FRIENDS_VIDEOS = getFriendsVideosDir();
//	public static String PATH_FRIENDS_VIDEOS = getFriendsVideosPath();
	
	// My thumbnails - Pictures/Instacake/Me directory
	public static File DIR_MY_THUMBS = getMyThumbsDir();
//	public static String PATH_MY_THUMBS = getMyThumbsPath();
	
	// My friends' thumbnails - Pictures/Instacake/Friends directory
	public static File DIR_FRIENDS_THUMBS = getFriendsThumbsDir();
//	public static String PATH_FRIENDS_THUMBS = getFriendsThumbsPath();
	
	// My projects - Movies/Instacake/Projects directory
	private static File getProjectsDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Instacake/Projects");
		if (!dir.exists()){
			if (!dir.mkdirs()){
				Log.v("getMyProjectsDir", "Movies/Instacake/Projects does not exist and cannot be created");
				return null;
			}
		}
		return dir;
	}
	private static String getProjectsPath() {
		return DIR_PROJECTS.getPath() + File.separator + "projects.txt";
	}
	
	// My videos - Movies/Instagram directory
	private static File getMyVideosDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Instagram");
		if (!dir.exists()){
			if (!dir.mkdirs()){
				Log.v("getMyVideosDir", "Movies/Instagram does not exist and cannot be created");
				return null;
			}
		}
		return dir;
	}
	
	// My friends' videos - Movies/Instacake directory
	private static File getFriendsVideosDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Instacake");
		if (!dir.exists()){
			if (!dir.mkdirs()){
				Log.v("getFriendsVideosDir", "Movies/Instacake does not exist and cannot be created");
				return null;
			}
		}
		return dir;
	}
	
	// My thumbnails - Pictures/Instacake/Me directory
	private static File getMyThumbsDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Instacake/Me");
		if (!dir.exists()){
			if (!dir.mkdirs()){
				Log.v("getMyThumbsDir", "Pictures/Instacake/Me does not exist and cannot be created");
				return null;
			}
		}
		return dir;
	}
	
	// My friends' thumbnails - Pictures/Instacake/Friends directory
	private static File getFriendsThumbsDir() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Instacake/Friends");
		if (!dir.exists()){
			if (!dir.mkdirs()){
				Log.v("getFriendsThumbsDir", "Pictures/Instacake/Friends does not exist and cannot be created");
				return null;
			}
		}
		return dir;
	}
	
	
	
	
	// Project path - Movies/Instacake/Projects/proj_id.json
	public static String getProjectPath(String projectId) {
		return DIR_PROJECTS.getPath() + File.separator + projectId + ".json";
	}
	
//	// Gets filepath of one thumbnail in Instagram pictures folder
//	public static String getMyThumbnailFilePath(String created_time) {
//		return DIR_MY_THUMBS.getPath() + File.separator + "IMG_" + created_time + ".jpg";
//	}
//	
	public static String getThumbnailFilePath(String thumbnailId) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "Instacake");
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("getVideoFilePath", "Failed to create Instacake/Videos directory");
				return null;
			}
		}

		return mediaStorageDir.getPath() + File.separator + "img_" + thumbnailId + ".bmp";
	}
//	
	public static String getVideoFilePath(String videoId) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_MOVIES), "Instacake");
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("getVideoFilePath", "Failed to create Instacake/Videos directory");
				return null;
			}
		}
		
		return mediaStorageDir.getPath() + File.separator + "vid_" + videoId + ".mp4";
	}

	public static String getAccessTokenRequestBody(String requestToken) {
		return "client_id=" + Constants.CLIENT_ID +
				"&client_secret=" + Constants.CLIENT_SECRET +
				"&grant_type=authorization_code" +
				"&redirect_uri=" + Constants.CALLBACK_URL +
				"&code=" + requestToken;
	}
	
	public static String getFeedUrl(String accessToken){
		return "https://api.instagram.com/v1/users/self/feed?access_token=" + accessToken;
	}
}