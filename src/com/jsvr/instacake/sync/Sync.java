package com.jsvr.instacake.sync;

import android.app.DownloadManager;

import com.jsvr.instacake.rails.RailsClient;

public class Sync {
	public static void syncProjectsFile(String instaId, String accessToken, DownloadManager dm){
		RailsClient.syncProjectsFile(instaId, accessToken, dm);
	}

	public static void syncProject(String projectId, String accessToken, DownloadManager dm) {
		RailsClient.syncProject(projectId, accessToken, dm);
	}


}
