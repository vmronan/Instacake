package com.jsvr.instacake.sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.app.DownloadManager;
import android.util.Log;

import com.jsvr.instacake.data.Project;
import com.jsvr.instacake.gram.GramClient;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;
import com.jsvr.instacake.rails.RailsJSONManager;

public class Sync {
	
	public static final int RESPONSE_OK = 200;
	public static final int ERROR = -1;
	
	public interface SyncCallback {
		void callbackCall(int statusCode, Object response);
	}

	public static void syncProject(final String projectUid, 
								   final String accessToken, 
								   final DownloadManager dm,
								   final SyncCallback refreshProjectOnUiThread) {
		/*
		 * In order to sync a project:
		 *  1. Get the project from rails
		 *  2. Get the list of videos to download by comparing local and rails videos
		 *  3. Download missing videos one at a time and add videos to project
		 *  4. Save project locally
		 *  5. Refresh UI thread
		 */
		
		final SyncCallback projectReadyForSaveAfterDownloads = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject){
				Project projectToSave = (Project) responseObject;
				
				if (statusCode == RESPONSE_OK){
					LocalClient.saveProject(projectToSave);
					refreshProjectOnUiThread.callbackCall(RESPONSE_OK, 
							"Project + " + projectToSave.getTitle() + " has been saved.");
				}
			}
		};
		
		SyncCallback projectReturnedFromRailsClient = new SyncCallback() {
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				Project project = (Project) responseObject;
				if(statusCode == RESPONSE_OK) {
					ArrayList<String> videoUidsToDownload = getVideoUidsForDownload(project.getVideoUids(), projectUid);
					GramClient.downloadVideosOneAtATime(project,
														videoUidsToDownload, 
														false,
														accessToken,
														dm, 
														projectReadyForSaveAfterDownloads);
				}
			}
		};
		
		RailsClient.getProject(projectUid, projectReturnedFromRailsClient);
	}

	public static void updateMyProjects(final String userUid,
										final String accessToken, 
										final DownloadManager dm,
										final SyncCallback updateProjectsListOnUiThread) {
		/* 1. Get the project list from RailsClient
		 * 2. Compare with project list returned by LocalClient.
		 * 3. Update the project and download necessary videos/thumbs
		 * 4. Throw a callback to the UI thread to update the list of projects.
		 */
		SyncCallback projectListReturnedFromRailsClient = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String response = (String) responseObject;
				if (statusCode == RESPONSE_OK){
					// response contains userUid, so we save
					ArrayList<String> projectsToUpdate = getListOfNewProjects(response);
					for (String projectUid : projectsToUpdate){
						syncProject(projectUid, 
								   	accessToken,
								   	dm,
								    new SyncCallback(){
										@Override
										public void callbackCall(int statusCode, Object response){};
									});
						Log.v("projectListReturnedFromRailsClient", "trying to sync project " + projectUid);
					}
					updateProjectsListOnUiThread.callbackCall(RESPONSE_OK, "Tried to update " + projectsToUpdate.size() + " projects.");
				} else if (statusCode == ERROR){
				} else {
					// Really bad error...
				}
			}
		};
		
		RailsClient.getProjectsList(userUid, projectListReturnedFromRailsClient);	
	}
	
	// Downloads Instagram movies to Movies/Instacake/Me directory
	public static void updateMyMovies(final String accessToken,
									  final DownloadManager dm,
									  final SyncCallback refreshVideosOnUiThread){
		/* In order to update my videos:
		 *  1. Get the list of videos from the GramClient
		 *  2. Compare the list of videos found in my video folder.
		 *  3. Download the videos I am missing.
		 *  4. Update the UI thread with the given SyncCallback Object
		 */
		
		final SyncCallback UiReadyForUpdateAfterDownloads = new SyncCallback(){
			@Override 
			public void callbackCall(int statusCode, Object responseObject){
				String response = (String) responseObject;
				if (statusCode == RESPONSE_OK){
					refreshVideosOnUiThread.callbackCall(RESPONSE_OK, response);
				}
			}
		};
		
		SyncCallback instaVideoListReturned = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject){
				String response = (String) responseObject;
				//TODO: track and implement statusCode properly
				if (statusCode == RESPONSE_OK){
					System.out.println("Going to download all of the videos here: " + response);
					GramClient.downloadVideosOneAtATime(getMyVideoUidsForDownloading(response),
														true,
														accessToken, 
														dm, 
														UiReadyForUpdateAfterDownloads);
				}
			}
		};
		
		GramClient.getMyMovieList(accessToken, instaVideoListReturned);
	}

	public static String createProject(String title, final String userUid, final String username, final SyncCallback projectHasBeenCreated){
		// TODO: Verify uniqueness of uid's
		final String projectUid = Integer.toString(new Random().nextInt());
		
		// Set up listener for adding user to project
		final SyncCallback userAddedToNewProjectInRails = new SyncCallback() {
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String response = (String) responseObject;
				if(statusCode == RESPONSE_OK) {
					System.out.println("Done adding user.");
					projectHasBeenCreated.callbackCall(RESPONSE_OK, response);
				}
			}
		};
		
		// Set up listener
		SyncCallback createdProjectInRails = new SyncCallback() {
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				if(statusCode == RESPONSE_OK) {
					System.out.println("Done creating project.");
					RailsClient.addUserToProject(userUid, projectUid, username, userAddedToNewProjectInRails);
				}
			}
		};
		
		// Save data locally and in the cloud.
		LocalClient.createProject(projectUid, title, userUid, username);
		RailsClient.createProject(projectUid, title, userUid, username, createdProjectInRails);
		
		return projectUid;
	}

	public static void addUserToProject(final String newUsername, String accessToken, final String projectUid, final SyncCallback updateUsersOnUiThread){
		/* Since users are free to change their instagram username at anytime,
		 * we save the unique (unchanging) userUid along with the current username
		 * when we add a user to a project.
		 * 
		 * We add a user in the following way:
		 * 	1. Use GramClient to find userUid
		 *  2. Listen for the response from GramClient that contains the userUid
		 *  3. Save data through LocalClient and RailsClient
		 *  4. Throw a callback to the UI thread so that ViewProjectActivity updates Users list
		 */
		
		// Set up listener
		SyncCallback foundUserUid = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String response = (String) responseObject;
				if (statusCode == RESPONSE_OK){
					// response contains userUid, so we save
					RailsClient.addUserToProject(response, projectUid, newUsername, new SyncCallback() {
						@Override
						public void callbackCall(int statusCode, Object responseObject) {}
					});
					LocalClient.addUserToProject(response, projectUid, newUsername);
					updateUsersOnUiThread.callbackCall(RESPONSE_OK, "User should now be added to the project.");
				} else if (statusCode == ERROR){
					// response contains error message
					Log.e("foundUserUid", "Had statusCode == ERROR... response was " + response);
					updateUsersOnUiThread.callbackCall(ERROR, "User was not added to the project.");
				} else {
					// Really bad error...
				}
			}
		};
		
		GramClient.getUserUid(newUsername, accessToken, foundUserUid);
	}

	public static void addVideoToProject(String thumbnailPath, String videoUid, String projectUid, String userUid) {
		LocalClient.addVideoByThumbnailPath(thumbnailPath, projectUid);
		RailsClient.addVideoToProject(projectUid, userUid, "created some time ago", videoUid);
	}

	protected static ArrayList<String> getListOfNewProjects(String response) {
		ArrayList<String> railsProjectsList = RailsJSONManager.parseForProjectsList(response);
		ArrayList<String> localProjectsList = LocalClient.getProjectUids();
		ArrayList<String> newProjects = new ArrayList<String>();
		for (String railsProject : railsProjectsList){
			if (!localProjectsList.contains(railsProject)){
				newProjects.add(railsProject);
			}
		}
		return newProjects;
	}

	protected static ArrayList<String> getMyVideoUidsForDownloading(String response) {
		String[] gramVideoUids = response.split("[\\r\\n]+");
		ArrayList<String> localVideoUids = LocalClient.getMyVideoUids();
		
		ArrayList<String> videoUidsForDownload = new ArrayList<String>();
		for (String gramVid : gramVideoUids){
			if (!localVideoUids.contains(gramVid)){
				// Our Local Storage is not aware of this video, so we download it
				videoUidsForDownload.add(gramVid);
			}
		}
		return videoUidsForDownload;
	}
	
	protected static ArrayList<String> getProjectUserList(String response) {
		return new ArrayList<String>(Arrays.asList(response.split("[\\r\\n]+")));
	}

	// Compare rails data against local data to find videos that need to be downloaded.
	private static ArrayList<String> getVideoUidsForDownload(ArrayList<String> railsVideoUids, String projectUid) {
		if (LocalClient.getProject(projectUid).getTitle().equals("temporary title")){
			System.out.println("added project " + projectUid);
		}
		ArrayList<String> localVideoUids = LocalClient.getVideoUidsForProject(projectUid);
		
		ArrayList<String> videoUidsForDownload = new ArrayList<String>();
		for (String railsVid : railsVideoUids){
			if (!localVideoUids.contains(railsVid)){
				// Our Local Storage is not aware of this video, so we download it
				videoUidsForDownload.add(railsVid);
			}
		}
		return videoUidsForDownload;
	}
	
	
}