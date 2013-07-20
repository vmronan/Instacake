package com.jsvr.instacake.sync;

import android.app.DownloadManager;

import com.jsvr.instacake.rails.RailsClient;

public class Sync {
	public static void syncAllProjects(String instaId, String accessToken, DownloadManager dm){
		RailsClient.syncAllProjects(instaId, accessToken, dm);
	}

	public static void syncProject(String projectId, String accessToken, DownloadManager dm) {
		RailsClient.syncProject(projectId, accessToken, dm);
	}


}
