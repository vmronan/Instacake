package com.jsvr.instacake.gram;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;

import com.jsvr.instacake.rails.RestClient;
import com.loopj.android.http.RequestParams;

public class GramClient {
	
	private static final String BASE_URL = "https://api.instagram.com/v1/";
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
	
	public static void getMediaInfo(String mediaId, String accessToken) {
		RequestParams params = new RequestParams();
		params.put("access_token", accessToken);
		RestClient.get(getAbsoluteUrl("/media/" + mediaId), params, RestClient.getResponseHandler("getMediaInfo"));
	}

	private class GramJSONManager {
		
		public JSONObject parseMediaResponse(String response){
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObject;
		}
		
		public Uri getVidUriFromJson(JSONObject json){
			Uri uri = null;
			try {
				uri = Uri.parse(json.getJSONObject("data").getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}
		
		public Uri getThumbUriFromJson(JSONObject json){
			Uri uri = null;
			try {
				uri = Uri.parse(json.getJSONObject("data").getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}
		
		
	}
}
