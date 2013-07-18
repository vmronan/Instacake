package com.jsvr.instacake.data;

import java.io.File;
import java.util.Random;

import android.os.Environment;
import android.util.Log;

public class Video {
	
	private String vidUrl;
	private String lowResVidUrl;
	private String user_id;
	private String thumbnailUrl;
	private String localVidUrl;
	private String localThumbnailUrl;
	private String uid;
	
	public Video(String vidUrl, String lowResVidUrl, String user_id,
			String thumbnailUrl, String localVidUrl, String localThumbnailUrl,
			String uid) {
		super();
		this.vidUrl = vidUrl;
		this.lowResVidUrl = lowResVidUrl;
		this.user_id = user_id;
		this.thumbnailUrl = thumbnailUrl;
		this.uid = generateUid();
		this.localVidUrl = generateLocalVidUrl(uid);
		this.localThumbnailUrl = generatelocalThumbnailUrl(uid);
	}
	
	private String generateUid(){
		return Integer.toString((new Random()).nextInt());
	}
	
	private String generateLocalVidUrl(String uid){
		
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_MOVIES), "Instacake/Videos");
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("getVideoFilePath", "Failed to create Instacake/Videos directory");
				return null;
			}
		}
		
		return mediaStorageDir.getPath() + File.separator + "vid_" + uid + ".mp4";

	}

	public static String generatelocalThumbnailUrl(String thumbnailId) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_MOVIES), "Instacake/Videos");
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("getVideoFilePath", "Failed to create Instacake/Videos directory");
				return null;
			}
		}
		// TODO is this a jpg?
		return mediaStorageDir.getPath() + File.separator + "img_" + thumbnailId + ".jpg";
	}
	
	public String getVidUrl() {
		return vidUrl;
	}

	public String getLowResVidUrl() {
		return lowResVidUrl;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getLocalVidUrl() {
		return localVidUrl;
	}

	public String getLocalThumbnailUrl() {
		return localThumbnailUrl;
	}

	public String getUid() {
		return uid;
	}
	
}
