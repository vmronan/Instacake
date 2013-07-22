package com.jsvr.instacake.rails;

import java.util.ArrayList;

import android.util.Log;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RailsClient {
	
	private static String mUserUid;
	private static String mUsername;
//	private static DownloadManager mDM;
//	private static String mAccessToken;
	
	private static final String BASE_URL = "http://54.218.123.27:3000/";
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
	
	public static void createUser(String userUid, String username) {
		RequestParams params = new RequestParams();
		params.put("user[uid]", userUid);
		params.put("user[username]", username);
		RestClient.post(getAbsoluteUrl("users/create"), params, RestClient.getResponseHandler("createUser"));
	}

	// Create project and add first user
	public static void createProject(String projectUid, String title, String userUid, String username) {
		if (userUid.equals(Constants.ERROR)){
			Log.v("createProject", "failed to find valid userUid");
			return;
		}
		
		mUserUid = userUid;
		mUsername = username;
		RequestParams params = new RequestParams();
		params.put("project[uid]", projectUid);
		params.put("project[title]", title);		
		RestClient.post(getAbsoluteUrl("projects/create"), params, createProjectHandler);
	}


	
	// Handle project creation... if successful, adds first user to new project
	public static AsyncHttpResponseHandler createProjectHandler = new AsyncHttpResponseHandler(){
		String tag = "newProject";
		@Override
		public void onSuccess(String response) {
			super.onSuccess(response);
			Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
			addUserToProject(mUserUid, RailsJSONManager.getProjectUidFromResponse(response), mUsername);
		}
		@Override
		public void onFailure(Throwable e, String response) {
			Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
			e.printStackTrace();
			super.onFailure(e, response);
		}
	};
	
	// Adds user to project
	public static void addUserToProject(String userUid, String projectUid, String newUsername) {
		if (projectUid.equals(Constants.ERROR)){
			Log.v("addUserToProject", "project uid is not valid");
			return;
		}
		
		RequestParams params = new RequestParams();
		params.put("user_uid", userUid);
		params.put("username", newUsername);
		params.put("project_uid", projectUid);
		RestClient.post(getAbsoluteUrl("projects/add_user"), params, addUserHandler);
	}
	
	// Handles response from rails
	public static AsyncHttpResponseHandler addUserHandler = new AsyncHttpResponseHandler(){
		String tag = "addUser";
		@Override
		public void onSuccess(String response) {
			super.onSuccess(response);
			Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
		}
	
		@Override
		public void onFailure(Throwable e, String response) {
			Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
			e.printStackTrace();
			super.onFailure(e, response);
		}
	};
	
	
	public static void addVideoToProject(String projectId, String userUid, String createdAt, String videoUid){
		RequestParams params = new RequestParams();
		params.put("user_uid", userUid);
		params.put("project_uid", projectId);
		params.put("video[created_at]", createdAt);
		params.put("video[uid]", videoUid);
		
		RestClient.post(getAbsoluteUrl("projects/create_video_and_add_to_project"), params, RestClient.getResponseHandler("addVideoToProject"));
	}
	
	public static void getProjectsList(String userUid, final SyncCallback projectListReturnedFromRailsClient) {
		RequestParams params = new RequestParams();
		params.put("user_uid", userUid);
		
		RestClient.post(getAbsoluteUrl("projects/get_projects_list"), params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response){
				super.onSuccess(response);
				projectListReturnedFromRailsClient.callbackCall(Sync.RESPONSE_OK, response);
			}
		});
	}
	
	// Gets userUids
	public static void getUserUidsForProject(String projectUid, final SyncCallback userUidsReturnedFromRailsClient) {
		RequestParams params = new RequestParams();
		params.put("project_uid", projectUid);
		
		RestClient.post(getAbsoluteUrl("projects/get_users_for_project"), params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				ArrayList<String> railsUserUids = RailsJSONManager.getUserUidsForProjectFromResponse(response);
				String userUidsForProject = "";
				for (String line : railsUserUids){
					Log.v("getUsersForProject... railsclient", "adding line " + line);
					userUidsForProject = userUidsForProject.concat(line + "\n");
				}
				userUidsReturnedFromRailsClient.callbackCall(Sync.RESPONSE_OK, userUidsForProject);
			}
		});
	}
	
	// Gets usernames
		public static void getUsernamesForProject(String projectUid, final SyncCallback usernamesReturnedFromRailsClient) {
			RequestParams params = new RequestParams();
			params.put("project_uid", projectUid);
			
			RestClient.post(getAbsoluteUrl("projects/get_users_for_project"), params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String response) {
					super.onSuccess(response);
					ArrayList<String> railsUserUids = RailsJSONManager.getUsernamesForProjectFromResponse(response);
					String usernamesForProject = "";
					for (String line : railsUserUids){
						Log.v("getUsersForProject... railsclient", "adding line " + line);
						usernamesForProject = usernamesForProject.concat(line + "\n");
					}
					usernamesReturnedFromRailsClient.callbackCall(Sync.RESPONSE_OK, usernamesForProject);
				}
			});
		}

	public static void getVideosForProject(String projectUid, final SyncCallback videoUidsForProjectReturned) {
		RequestParams params = new RequestParams();
		params.put("project_uid", projectUid);
		
		RestClient.post(getAbsoluteUrl("projects/get_videos_for_project"), params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response){
				super.onSuccess(response);
				ArrayList<String> railsVideoUids = RailsJSONManager.getVideosForProjectFromResponse(response);
				String videoUidsForProject = "";
				for (String line : railsVideoUids){
					videoUidsForProject = videoUidsForProject.concat(line + "\n");
				}
				videoUidsForProjectReturned.callbackCall(Sync.RESPONSE_OK, videoUidsForProject);
			}
		});
		
	}

//	public static void syncAllProjects(String instaId, String accessToken, DownloadManager dm) {
//		mAccessToken = accessToken;
//		mDM = dm;
//		mUserUid = instaId;
//		RequestParams params = new RequestParams();
//		params.put("insta_id", mUserUid);
//		
//		RestClient.post(getAbsoluteUrl("projects/get_projects_list"), params, new AsyncHttpResponseHandler(){
//			@Override
//			public void onSuccess(String response){
//				super.onSuccess(response);
//				ArrayList<String> myRailsProjects = RailsJSONManager.getProjectListFromResponse(response);
//				ArrayList<String> myLocalProjects = LocalClient.readProjectsFile();
//				for (String project : myRailsProjects){
//					if (!myLocalProjects.contains(project)){
//						System.out.println("Creating project with id " + project);
//						LocalClient.createProject(project, "some title", mUserUid);
//					}
//					Sync.syncProject(project, mAccessToken, mDM);
//				}
//			}
//		});
//		
//	}
//
//	public static void syncProject(final String projectUid, String accessToken, DownloadManager dm) {
//		mAccessToken = accessToken;
//		mDM = dm;
//		
//		RequestParams params = new RequestParams();
//		params.put("project_uid", projectUid);
//		RestClient.post(getAbsoluteUrl("projects/get_videos_for_project"), params, new AsyncHttpResponseHandler(){
//			@Override
//			public void onSuccess(String response){
//				super.onSuccess(response);
//				Log.v("syncProject " + projectUid, "onSuccess() has response \n" + response);
//				ArrayList<String> videos = RailsJSONManager.getVideosForProjectFromResponse(response);
//				for (String video : videos){
//					GramClient.syncMovie(video, mAccessToken, mDM, false);
//					LocalClient.addVideoToProject(video, projectUid);
//				}
//			}
//			
//			@Override
//			public void onFailure(Throwable e, String response) {
//				Log.v("syncProject " + projectUid, "onFailure() has response \n" + response);
//				e.printStackTrace();
//				super.onFailure(e, response);
//			}
//		});
//		
//	}
	
}