package com.jsvr.instacake.data;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class Constants {
	
	/* Shared Prefs */
	public static final String PREFS_NAME = "com.jsvr.instacake.SHARED_PREFS";
	public static final String ACCESS_TOKEN_KEY = "yodawgi'mtheaccesstokenkey";
	public static final String USER_UID_KEY = "yodawgi'mliketheinstagramidyouget";
	public static final String USERNAME_KEY = "yodawgizlykugottahaveausernamenaaaahmsayin??";
	public static final String ERROR = "Key or Value Error";
	
	/* Instagram App Authorization */
	public static final String CLIENT_ID = "2441a48392cf4ab6a7343038f858ae15";
	public static final String CLIENT_SECRET = "781c8fe7656b4e03b8bf45a0990a1331";
	
	/* Instagram API Strings */
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	public static final String ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String API_URL =  "https://api.instagram.com/v1";
	public static String CALLBACK_URL = "http://www.vanessaronan.com";
	public static final String AUTH_URL_WITH_PARAMS = AUTH_URL + "?client_id=" + CLIENT_ID +
													  "&redirect_uri=" + CALLBACK_URL + "&response_type=code";
	
	/* Getters for Instagram API */
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
	
	/* Project Key and Helpers */
	public static final String PROJECT_UID_KEY = "yodawgi'maprojectid";
	
	public static String getProjectFilename(String projectUid) {
		return "PRJ_" + projectUid + ".json";
	}
	
	/* Critical Directories */
	private static final File MOVIES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
	private static final File PICTURES_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	private static final File projectsDir = new File(MOVIES_DIR, "Instacake/Projects");
	private static final File moviesDir = new File(MOVIES_DIR, "Instacake");
	private static final File myMoviesDir = new File(MOVIES_DIR, "Instacake/Me");
	private static final File friendsMoviesDir = new File(MOVIES_DIR, "Instacake/Friends");
	private static final File thumbsDir = new File(PICTURES_DIR, "Instacake");
	private static final File myThumbsDir = new File(PICTURES_DIR, "Instacake/Me");
	private static final File friendsThumbsDir = new File(PICTURES_DIR, "Instacake/Friends");
	
	

	/* Getters for Directories */
	public static File getProjectsDir() {
		return buildOrEnsureDirectory(projectsDir);
	}
	
	public static File getMyMoviesDir() {
		return buildOrEnsureDirectory(myMoviesDir);
	}
	
	public static File getFriendsMoviesDir() {
		return buildOrEnsureDirectory(friendsMoviesDir);
	}
	
	public static File getMyThumbsDir() {
		return buildOrEnsureDirectory(myThumbsDir);
	}
	
	public static File getFriendsThumbsDir() {
		return buildOrEnsureDirectory(friendsThumbsDir);
	}
	
	
	
	/* Getters for file paths */
	public static String getProjectPath(String projectUid){
		return getProjectsDir().getPath() + File.separator + getProjectFilename(projectUid);
	}
	
	public static String getThumbnailPath(String thumbnailId, boolean isMine) {
		if (isMine){
			return getMyThumbsDir().getPath() + File.separator + "IMG_" + thumbnailId + ".jpg";
		} else {
			return getFriendsThumbsDir().getPath() + File.separator + "IMG_" + thumbnailId + ".jpg";
		}
	}
	
	public static String getMoviesPath(String movieId, boolean isMine) {
		if (isMine){
			return getMyMoviesDir().getPath() + File.separator + "VID_" + movieId + ".mp4";
		} else {
			return getFriendsMoviesDir().getPath() + File.separator + "VID_" + movieId + ".mp4";
		}
	}
	
	public static String getProjectsFilePath(){
		return getProjectsDir().getPath() + File.separator + "projects.txt";
	}

	
	/* Directory builders */
	private static File buildOrEnsureDirectory(File mediaStorageDir){
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("buildOrEnsureDirectory", "Failed to create directory: " + mediaStorageDir.getPath());
				return null;
			}
		}
		return mediaStorageDir;
	}

	public static boolean buildOrEnsureAllDirectories() {

		File[] directories = new File[] {moviesDir,
										 myMoviesDir,
										 friendsMoviesDir,
										 thumbsDir,
										 myThumbsDir,
										 friendsThumbsDir};
		
		for (File dir : directories){
			if (buildOrEnsureDirectory(dir) == null){
				return false;
			}
		}
		return true;
	}
	
	
	// Gets what's after the xxx_ and before the . in any filename
	public static String getIdFromFilename(String filename) {
		return (filename.split("\\.")[0]).substring(4);
	}
	
	public static String getVideoPathFromThumbnailPath(String thumbnailPath) {
		return thumbnailPath.replace("Pictures", "Movies").replace("IMG", "VID").replace(".jpg", ".mp4");
	}
}