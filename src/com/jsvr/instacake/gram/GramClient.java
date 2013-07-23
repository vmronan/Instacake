package com.jsvr.instacake.gram;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jsvr.instacake.data.Constants;
import com.jsvr.instacake.data.Project;
import com.jsvr.instacake.sync.Sync;
import com.jsvr.instacake.sync.Sync.SyncCallback;
import com.jsvr.instacake.sync.VideoSync;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GramClient {
	
	private static final String BASE_URL = "https://api.instagram.com/v1/";
	private static final String GET_THUMB = "getThumbnail";
	private static final String GET_MOVIE = "getMovie";
	private static final String SYNC_WITH_FEED = "syncWithFeed";
	public static final String NO_USER_FOUND = "No User was found";
	
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    
    private static AsyncHttpClient client = new AsyncHttpClient();
	private static Project mProject;
		
	public static void getThumbnail(String instaId, String accessToken, DownloadManager dm, Boolean isMine){
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/media/" + instaId), params, getGramClientResponseHandler(new String[]{GET_THUMB}, dm, instaId, isMine));
	}
	
	public static void getMovie(String instaId, String accessToken, DownloadManager dm, Boolean isMine){
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/media/" + instaId), params, getGramClientResponseHandler(new String[]{GET_MOVIE}, dm, instaId, isMine));
	}
	
	public static void syncMovie(String mediaId, String accessToken, DownloadManager dm, Boolean isMine){
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/media/" + mediaId), params, getGramClientResponseHandler(new String[]{GET_THUMB, GET_MOVIE}, dm, mediaId, isMine));
	}
	
	public static void syncWithMyFeed(String accessToken, DownloadManager dm){
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/users/self/feed"), params, getGramClientResponseHandler(new String[]{SYNC_WITH_FEED}, dm, "unnecessary", true));
	}

	public static AsyncHttpResponseHandler getGramClientResponseHandler (final String[] methods, 
																		 final DownloadManager dm, 
																		 final String mediaId, 
																		 final boolean isMine){
		final String tag = "GramClient";
		return new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Log.v(tag + " handler", "OnSuccess() has the reponse: /n" + response);
				for (String method : methods ){
					if (method.equals(GET_THUMB)){
				        Request request = new Request(GramJSONManager.getThumbUriFromJson(GramJSONManager.parseMediaResponse(response)));
				        request.setTitle("Downloading Thumbnail " + mediaId);
				        Constants.getThumbnailPath(mediaId, isMine); // To ensure that the directory exists.
						if (isMine){
							request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Instacake/Me/IMG_" + mediaId + ".jpg");
						} else {
							request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Instacake/Friends/IMG_" + mediaId + ".jpg");
						}
				        dm.enqueue(request); 
				        
					} else if (method.equals(GET_MOVIE)){
						Request request = new Request(GramJSONManager.getVidUriFromJson(GramJSONManager.parseMediaResponse(response)));
						request.setTitle("Downloading Video " + mediaId);
						Constants.getMoviesPath(mediaId, isMine); // To ensure that the directory exists.
						if (isMine){
							request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "Instacake/Me/VID_" + mediaId + ".mp4");
						} else {
							
							request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "Instacake/Friends/VID_" + mediaId + ".mp4");
						}
						dm.enqueue(request);
					} else if (method.equals(SYNC_WITH_FEED)){
						ArrayList<String> myVids = GramJSONManager.getMovieIdsFromJson(GramJSONManager.parseMediaResponse(response));
						System.out.println("There are " + myVids.size() + " video ids");
						for (String myVid : myVids){
							System.out.println("vid is " + myVid);
						}
						
						VideoSync.compareAndDownloadVids(myVids); 
					}
				}
				
			}

			@Override
			public void onFailure(Throwable e, String response) {
				Log.v(tag + " handler", "onFailure() has the response: \n" + response + "\n\n");
				e.printStackTrace();
				super.onFailure(e, response);
			}
		};
	}
	
	private static class GramJSONManager {
		
		public static JSONObject parseMediaResponse(String response){
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObject;
		}
		
		public static Uri getVidUriFromJson(JSONObject json){
			Uri uri = null;
			try {
				uri = Uri.parse(json.getJSONObject("data").getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}
		
		public static Uri getThumbUriFromJson(JSONObject json){
			Uri uri = null;
			try {
				uri = Uri.parse(json.getJSONObject("data").getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return uri;
		}
		
		public static ArrayList<String> getMovieIdsFromJson(JSONObject json){
			ArrayList<String> myMovies = new ArrayList<String>();
			try {
				// Response is an array of feed items, both video and image.
				JSONArray array = json.getJSONArray("data");
				int length = array.length();
				for (int i=0; i<length; i++){
					JSONObject feedItem = array.getJSONObject(i);
					if (feedItem.getString("type").equals("video")){
						myMovies.add(feedItem.getString("id"));
					}
				}				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return myMovies;
		}

		public static String getUserUidFromJson(JSONObject json, String desiredUsername) {
			try{
				// Response is an array of similar usernames.
				// TODO: Allow user to choose correct user from these usernames.
				JSONArray array = json.getJSONArray("data");
				int length = array.length();
				for (int i=0; i<length; i++){
					JSONObject user = array.getJSONObject(i);
					//TODO: toLowerCase() threw a warning regarding locale, which I accidentally suppressed in this file.
					if (user.getString("username").toLowerCase().equals(desiredUsername.toLowerCase())){
						return user.getString("id");
					}
				}
				return NO_USER_FOUND;
				
			} catch (JSONException e){
				e.printStackTrace();
			}
			return null;
		}
	}

	public static void getUserUid(final String newUsername, String accessToken, final SyncCallback foundUserUid) {
		// Asynchronously find UserUid and pass it through the provided SyncCallback
		RequestParams params = new RequestParams();
		params.put("q", newUsername);
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("users/search"), params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response){
				super.onSuccess(response);
				String userUid = GramJSONManager.getUserUidFromJson(GramJSONManager.parseMediaResponse(response), newUsername);
				if (userUid.equals(NO_USER_FOUND)){
					foundUserUid.callbackCall(Sync.ERROR, NO_USER_FOUND);
				} else {
					foundUserUid.callbackCall(Sync.RESPONSE_OK, userUid);
				}
				
			}
			
			@Override
			public void onFailure(Throwable e, String response){
				super.onFailure(e, response);
				e.printStackTrace();
				foundUserUid.callbackCall(Sync.ERROR, response);
			}
		});
		
	}

	// Download a video and its thumbnail
	public static void download(final String videoUid, 
							     String accessToken, 
							     final SyncCallback moveToNextVideo,
							     final DownloadManager dm,
							     final boolean isMine) {
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/media/" + videoUid), params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				
				// Download the thumbnail
				Request requestForThumb = new Request(GramJSONManager.getThumbUriFromJson(GramJSONManager.parseMediaResponse(response)));
				requestForThumb.setTitle("Downloading Thumbnail " + videoUid);
		        Constants.getThumbnailPath(videoUid, false); // To ensure that the directory exists.
				if (isMine){
					requestForThumb.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Instacake/Me/IMG_" + videoUid + ".jpg");
				} else {
					requestForThumb.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Instacake/Friends/IMG_" + videoUid + ".jpg");
				}
		        dm.enqueue(requestForThumb); 
		        
		        // Download the video
				Request requestForVid = new Request(GramJSONManager.getVidUriFromJson(GramJSONManager.parseMediaResponse(response)));
				requestForVid.setTitle("Downloading Video " + videoUid);
				Constants.getMoviesPath(videoUid, false); // To ensure that the directory exists.
				if (isMine){
					requestForVid.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "Instacake/Me/VID_" + videoUid + ".mp4");
				} else {
					requestForVid.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "Instacake/Friends/VID_" + videoUid + ".mp4");
				}
				dm.enqueue(requestForVid); 
				
			}

			@Override
			public void onFailure(Throwable e, String response) {
				super.onFailure(e, response);
				e.printStackTrace();
			}
			
			@Override
			public void onFinish(){
				super.onFinish();
				moveToNextVideo.callbackCall(Sync.RESPONSE_OK, videoUid);
			}
		});
	}

	public static void getMyMovieList(String accessToken, final SyncCallback instaVideoListReturned) {
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/users/self/feed"), params, new AsyncHttpResponseHandler(){
			@Override 
			public void onSuccess(String response){
				ArrayList<String> myVids = GramJSONManager.getMovieIdsFromJson(GramJSONManager.parseMediaResponse(response));
				String gramVidsString = "";
				for (String vid : myVids){
					System.out.println("vid is " + vid);
					gramVidsString = gramVidsString.concat(vid + "\n");
				}
				instaVideoListReturned.callbackCall(Sync.RESPONSE_OK, gramVidsString);
			}
		});
		
	}

	public static void downloadVideosOneAtATime(final Project project,
												final ArrayList<String> videoUidsToDownload, 
												final boolean isMine,
												final String accessToken, 
												final DownloadManager dm,
												final SyncCallback projectReadyForSaveAfterDownloads) {
		mProject = project;
		Log.v("GramClient.downloadVideosOneAtATime", "dealing with project " + mProject.getTitle());
		
		SyncCallback moveToNextVideo = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String uidOfDownloadedVideo = (String) responseObject;
				Log.v("GramClient.downloadVideosOneAtATime", "uid of downloaded video: " + uidOfDownloadedVideo);
				
				// Check a video off the list
				videoUidsToDownload.remove(uidOfDownloadedVideo);
				mProject.addVideo(uidOfDownloadedVideo, isMine);
				Log.v("GramClient.downloadVideosOneAtATime", "num of video uids: " + mProject.getVideoUids().size());
				
				// Continue if necessary
				if (videoUidsToDownload.size() > 0){
					download(videoUidsToDownload.get(0), accessToken, this, dm, isMine);
				} else {
					Log.v("* GramClient.downloadVideosOneAtATime", "done downloading. about to call back to projectReadyForSaveAfterDownloads");
					for(String s : mProject.getThumbnailPaths()) {
						Log.v("* GramClient.downloadVideosOneAtATime", "thumbnail: " + s);
					}
					projectReadyForSaveAfterDownloads.callbackCall(Sync.RESPONSE_OK, mProject);
				}
			}
		};
		
		//TODO: Figure out why this is necessary
		if (videoUidsToDownload.size() > 0 && !videoUidsToDownload.get(0).equals("")){
			Log.v("GramClient.downloadVideosOneAtATime", "about to download");
			download(videoUidsToDownload.get(0), accessToken, moveToNextVideo, dm, isMine);
		} else {
			Log.v("~ GramClient.downloadVideosOneAtATime", "calling projectReadyForSaveAfterDownloads.callbackCall because there are no videos to download");
			// Sometimes we are sent here to download an empty list of videoUids
			projectReadyForSaveAfterDownloads.callbackCall(Sync.RESPONSE_OK, mProject);
		}
	}

	public static void downloadVideosOneAtATime(
			final ArrayList<String> videoUidsToDownload, 
			final boolean isMine,
			final String accessToken, 
			final DownloadManager dm,
			final SyncCallback uiReadyForUpdateAfterDownloads) {
		
		SyncCallback moveToNextVideo = new SyncCallback(){
			@Override
			public void callbackCall(int statusCode, Object responseObject) {
				String uidOfDownloadedVideo = (String) responseObject;
				
				// Check a video off the list
				videoUidsToDownload.remove(uidOfDownloadedVideo);
				
				// Continue if necessary
				if (videoUidsToDownload.size() > 0){
					download(videoUidsToDownload.get(0), accessToken, this, dm, isMine);
				} else {
					uiReadyForUpdateAfterDownloads.callbackCall(Sync.RESPONSE_OK, "All videos downloaded.");
				}
			}
		};
		
		//TODO: Figure out why this is necessary
		if (videoUidsToDownload.size() > 0 && !videoUidsToDownload.get(0).equals("")){
			download(videoUidsToDownload.get(0), accessToken, moveToNextVideo, dm, isMine);
		} else {
			// Sometimes we are sent here to download an empty list of videoUids
			uiReadyForUpdateAfterDownloads.callbackCall(Sync.RESPONSE_OK, "No new videos to download.");
		}
	}
}