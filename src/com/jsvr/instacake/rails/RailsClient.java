package com.jsvr.instacake.rails;

import android.util.Log;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RailsClient {
	
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
	public static void createProject(String projectUid, String title, String userUid, String username, final SyncCallback createdProjectInRails) {
		if (userUid.equals(Constants.ERROR)){
			Log.v("createProject", "failed to find valid userUid");
			return;
		}
		
		RequestParams params = new RequestParams();
		params.put("project[uid]", projectUid);
		params.put("project[title]", title);
		
		RestClient.post(getAbsoluteUrl("projects/create"), params, new AsyncHttpResponseHandler() {
			String tag = "newProject";
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
				createdProjectInRails.callbackCall(Sync.RESPONSE_OK, response);
			}
			@Override
			public void onFailure(Throwable e, String response) {
				Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
				e.printStackTrace();
				super.onFailure(e, response);
			}
		});
	}

	// Adds user to project
	public static void addUserToProject(String userUid, String projectUid, String newUsername, final SyncCallback userAddedToNewProjectInRails) {
		if (projectUid.equals(Constants.ERROR)){
			Log.v("addUserToProject", "project uid is not valid");
			return;
		}
		
		RequestParams params = new RequestParams();
		params.put("user_uid", userUid);
		params.put("username", newUsername);
		params.put("project_uid", projectUid);
		RestClient.post(getAbsoluteUrl("projects/add_user"), params, new AsyncHttpResponseHandler() {
			String tag = "addUser";
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
				userAddedToNewProjectInRails.callbackCall(Sync.RESPONSE_OK, response);
			}
		
			@Override
			public void onFailure(Throwable e, String response) {
				Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
				e.printStackTrace();
				super.onFailure(e, response);
			}
		});
	}	
	
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
	
	// Get all info about project
	public static void getProject(String projectUid, final SyncCallback projectReturnedFromRailsClient) {
		RequestParams params = new RequestParams();
		params.put("project_uid", projectUid);
		
		RestClient.get(getAbsoluteUrl("projects/get_project"), params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Project project = RailsJSONManager.parseForProject(response);
				projectReturnedFromRailsClient.callbackCall(Sync.RESPONSE_OK, project);
			}
		});
	}
}