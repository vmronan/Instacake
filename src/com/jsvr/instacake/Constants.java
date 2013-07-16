package com.jsvr.instacake;

public class Constants {
	
	public static final String PREFS_NAME = "com.jsvr.instacake.SHARED_PREFS";

	public static final String CLIENT_ID = "2441a48392cf4ab6a7343038f858ae15";
	public static final String CLIENT_SECRET = "781c8fe7656b4e03b8bf45a0990a1331";
	
	private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
	public static final String API_URL =  "https://api.instagram.com/v1";
	public static String CALLBACK_URL = "http://www.vanessaronan.com";
	
	public static final String AUTH_URL_WITH_PARAMS = AUTH_URL + "?client_id=" + CLIENT_ID +
			"&redirect_uri=" + CALLBACK_URL + "&response_type=code";
	
	public static String getAccessTokenRequestBody(String requestToken) {
		return "client_id=" + Constants.CLIENT_ID +
				"&client_secret=" + Constants.CLIENT_SECRET +
				"&grant_type=authorization_code" +
				"&redirect_uri=" + Constants.CALLBACK_URL +
				"&code=" + requestToken;
	}
 
	public static final String ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	
	
}


