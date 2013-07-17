package com.jsvr.instacake;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RailsClient {
	
	private static final String BASE_URL = "http://54.218.123.27:3000";
	private static String mInstaId;
	
	public static AsyncHttpResponseHandler newProjectHandler = new AsyncHttpResponseHandler(){
		String tag = "newProject";
		@Override
		public void onSuccess(String response) {
			Log.v(tag + " response handler", "onSuccess() has the response: \n" + response);
			addFirstUserToProject(getProjectFromResponse(response));
		}

		private String getProjectFromResponse(String response) {
			return "hey d00d I still need to parse the response";
			// need to save project_id here
		}

		@Override
		public void onFailure(Throwable e, String response) {
			Log.v(tag + " response handler", "onFailure() has the response: \n" + response + "\n\n");
			e.printStackTrace();
			super.onFailure(e, response);
		}
	};
		

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

	public static void addUserToProject(String mInstaId2, String project_id) {
		// TODO Auto-generated method stub
		
	}
}
