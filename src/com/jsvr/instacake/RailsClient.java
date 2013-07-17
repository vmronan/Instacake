package com.jsvr.instacake;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RailsClient {
	
	private static final String BASE_URL = "http://54.218.123.27:3000";
	private static String mInstaId;

	public static void createProject(String title, String insta_id) {
		if (insta_id.equals("NOKEY")){
			Log.v("createProject", "failed to find valid insta_id");
			return;
		}
		
		mInstaId = insta_id;
		RequestParams params = new RequestParams();
		params.put("project[title]", title);		
		RestClient.post(getAbsoluteUrl("/projects/create"), params, newProjectHandler);
	}
    
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

	private static void addFirstUserToProject(String project_id) {
		addUserToProject(mInstaId, project_id);
	}

	public static void addUserToProject(String instaId, String projectId) {
		if (projectId.equals("0")){
			Log.v("addUserToProject", "project id is not valid");
			return;
		}
		
		RequestParams params = new RequestParams();
		params.put("insta_id", instaId);
		params.put("project_id", projectId);
		RestClient.post(getAbsoluteUrl("/projects/add_user"), params, addUserHandler);
		
	}
	
	public static AsyncHttpResponseHandler newProjectHandler = new AsyncHttpResponseHandler(){
		String tag = "newProject";
		@Override
		public void onSuccess(String response) {
			Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
			addFirstUserToProject(getProjectIdFromResponse(response));
		}

		private String getProjectIdFromResponse(String response) {
			try {
				JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
				String projectId = ((JSONObject) new JSONTokener(jsonObject.getString("project")).nextValue()).getString("id");
				System.out.println(projectId);
				return projectId;
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "0";
		}

		@Override
		public void onFailure(Throwable e, String response) {
			Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
			e.printStackTrace();
			super.onFailure(e, response);
		}
	};
	
	public static AsyncHttpResponseHandler addUserHandler = new AsyncHttpResponseHandler(){
		String tag = "addUser";
		@Override
		public void onSuccess(String response) {
			Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
		}

		@Override
		public void onFailure(Throwable e, String response) {
			Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
			e.printStackTrace();
			super.onFailure(e, response);
		}
	};

	public static void createUser(String instaId) {
		RequestParams params = new RequestParams();
		params.put("user[insta_id]", instaId);
		RestClient.post(getAbsoluteUrl("/users/create"), params, RestClient.getResponseHandler("createUser"));
	}
}
