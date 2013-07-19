package com.jsvr.instacake.gram;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jsvr.instacake.data.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GramClient {
	
	private static final String BASE_URL = "https://api.instagram.com/v1/";
	private static final String GET_THUMB = "getThumbnail";
	private static final String GET_MOVIE = "getMovie";
	
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
    
    private static AsyncHttpClient client = new AsyncHttpClient();
		
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
	
	public static void syncMovie(String instaId, String accessToken, DownloadManager dm, Boolean isMine){
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		client.get(getAbsoluteUrl("/media/" + instaId), params, getGramClientResponseHandler(new String[]{GET_THUMB, GET_MOVIE}, dm, instaId, isMine));
	}

	public static AsyncHttpResponseHandler getGramClientResponseHandler (final String[] methods, final DownloadManager dm, final String mediaId, final boolean isMine){
		final String tag = "GramClient";
		return new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
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
	}
}
