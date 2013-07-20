package com.jsvr.instacake.sync;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.DownloadManager;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.gram.GramClient;

public class VideoSync {
	
	private static String mAccessToken;
	private static DownloadManager mDM;
	
	public static String[] getMyVids(){
		String[] vidList = Constants.getMyMoviesDir().list();
		for (int i=0; i < vidList.length; i++){
			vidList[i] = Constants.getIdFromFilename(vidList[i]);
		}
		return vidList;
	}
	
	public static void compareAndDownloadVids(ArrayList<String> vidIdsFromMyFeed){
		String[] myVids = getMyVids();
		
		for (String vidId : vidIdsFromMyFeed){
			if (!Arrays.asList(myVids).contains(vidId)){
				GramClient.syncMovie(vidId, mAccessToken, mDM, true);
			}
		}
	}
	
	public static void syncWithMyFeed(String accessToken, DownloadManager dm){
		mAccessToken = accessToken;
		mDM = dm;
		GramClient.syncWithMyFeed(accessToken, dm);
	}
	
}
