package com.jsvr.instacake.sync;

import java.util.ArrayList;
import java.util.Random;

import android.app.DownloadManager;
import android.util.Log;

import com.jsvr.instacake.gram.GramClient;
import com.jsvr.instacake.local.LocalClient;
import com.jsvr.instacake.rails.RailsClient;
import com.jsvr.instacake.rails.RailsJSONManager;

public class Sync {
	
	public static final int RESPONSE_OK = 200;
	public static final int ERROR = -1;
	
	public interface SyncCallback {
		void callbackCall(int statusCode, String response);
	}
	
	
	public static void updateMyMovies(final String accessToken,
									  final DownloadManager dm,
									  final SyncCallback refreshVideosOnUiThread){
		/* In order to update my videos:
		 *  1. Get the list of videos from the GramClient
		 *  2. Compare the list of videos found in my video folder.
		 *  3. Download the videos I am missing.
		 *  4. Update the UI thread with the given SyncCallback Object
		 */
		
		SyncCallback instaVideoListReturned = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, String response){
				//TODO: track and implement statusCode properly
				if (statusCode == RESPONSE_OK){
					System.out.println("Going to download all of the videos here: " + response);
					GramClient.downloadVideosOneAtATime(getMyVideoUidsForDownloading(response), accessToken, dm, refreshVideosOnUiThread, true, "");
				}
			}
		};
		
		GramClient.getMyMovieList(accessToken, instaVideoListReturned);
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

	public static void syncProject(final String projectUid, 
								   final String accessToken, 
								   final DownloadManager dm, 
								   final SyncCallback refreshVideosOnUiThread) {
		/* In order to sync a project:
		 *  1. Get the list of videos in a project from the rails client, returned by a SyncCallback
		 *  2. From the listener, get the list of videos in a project from the LocalClient
		 *  3. Identify videos that need to be downloaded and download each with the GramClient
		 *  4. When the videos have been downloaded, send a callback to the UI thread to update the gridview
		 */
		
		SyncCallback videoUidsForProjectReturned = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, String response){
				//TODO: Track and check statusCode
				Log.v("videoUidsForProjectReturned", "response is " + response);
				ArrayList<String> videoUidsToDownload = getVideoUidsForDownload(response, projectUid);
				for (String video : videoUidsToDownload){
					Log.v("syncProject", "Wants to download " + video);
				}
				GramClient.downloadVideosOneAtATime(videoUidsToDownload, accessToken, dm, refreshVideosOnUiThread, false, projectUid);
			}
		};
		
		RailsClient.getVideosForProject(projectUid, videoUidsForProjectReturned);
	}
	
	// Compare rails data against local data to find videos that need to be downloaded.
	private static ArrayList<String> getVideoUidsForDownload(String response, String projectUid) {
		String[] railsVideoUids = response.split("[\\r\\n]+");
		if (LocalClient.getProject(projectUid).getTitle().equals("temporary title")){
			System.out.println("added project " + projectUid);
		}
		ArrayList<String> localVideoUids = LocalClient.getVideoUidsForProject(projectUid);
		
		for (String localVid : localVideoUids){
			Log.v("getVideoUidsForDownload", "localVid is " + localVid);
		}
		
		ArrayList<String> videoUidsForDownload = new ArrayList<String>();
		for (String railsVid : railsVideoUids){
			Log.v("getVideoUidsForDownlaod", "railsVid is " + railsVid);
			if (!localVideoUids.contains(railsVid)){
				// Our Local Storage is not aware of this video, so we download it
				videoUidsForDownload.add(railsVid);
			}
		}
		return videoUidsForDownload;
	}

	public static String createProject(String title, String userUid, String username){
		// TODO: Verify uniqueness of uid's
		String projectUid = Integer.toString(new Random().nextInt());
		
		// Save data locally and in the cloud.
		LocalClient.createProject(projectUid, title, userUid, username);
		RailsClient.createProject(projectUid, title, userUid, username);
		
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
			public void callbackCall(int statusCode, String response) {
				if (statusCode == RESPONSE_OK){
					// response contains userUid, so we save
					RailsClient.addUserToProject(response, projectUid, newUsername);
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
			public void callbackCall(int statusCode, String response) {
				if (statusCode == RESPONSE_OK){
					// response contains userUid, so we save
					ArrayList<String> projectsToUpdate = getListOfNewProjects(response);
					for (String projectUid : projectsToUpdate){
						syncProject(projectUid, 
								   	accessToken, 
								   	dm, 
								    new SyncCallback(){
							@Override
							public void callbackCall(int statusCode, String response){};
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

	protected static ArrayList<String> getListOfNewProjects(String response) {
		ArrayList<String> railsProjectsList = RailsJSONManager.getProjectListFromResponse(response);
		ArrayList<String> localProjectsList = LocalClient.readProjectsFile();
		ArrayList<String> newProjects = new ArrayList<String>();
		for (String railsProject : railsProjectsList){
			if (!localProjectsList.contains(railsProject)){
				newProjects.add(railsProject);
			}
		}
		return newProjects;
	}
	
	
}