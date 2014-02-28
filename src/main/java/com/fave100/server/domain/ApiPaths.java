package com.fave100.server.domain;

public class ApiPaths {

	// FaveList
	public static final String FAVELIST_ROOT = "favelist";
	public static final String GET_MASTER_FAVELIST = "/{list}";

	// Auth
	public static final String AUTH_ROOT = "auth";
	public static final String REGISTER = "/register";
	public static final String CREATE_APPUSER_FROM_GOOGLE_ACCOUNT = "/createAppUserFromGoogleAccount";
	public static final String CREATE_APPUSER_FROM_TWITTER_ACCOUNT = "/createAppUserFromTwitterAccount";
	public static final String CREATE_APPUSER_FROM_FACEBOOK_ACCOUNT = "/createAppUserFromFacebookAccount";
	public static final String LOGIN = "/login";
	public static final String LOGIN_WITH_GOOGLE = "/loginWithGoogle";
	public static final String LOGIN_WITH_TWITTER = "/loginWithTwitter";
	public static final String LOGIN_WITH_FACEBOOK = "/loginWithFacebook";
	public static final String LOGOUT = "/logout";
	public static final String GET_GOOGLE_AUTH_URL = "url/google";
	public static final String GET_FACEBOOK_AUTH_URL = "url/facebook";
	public static final String GET_TWITTER_AUTH_URL = "url/twitter";

	// Users
	public static final String USERS_ROOT = "users";
	public static final String GET_USER = "/{user}";
	public static final String GET_USERS_FAVELIST = "/{user}/favelists/{list}";
	public static final String GET_USERS_FOLLOWING = "/{user}/following";
	public static final String IS_GOOGLE_LOGGED_IN = "/google/loggedin";
	public static final String IS_APPUSER_LOGGED_IN = "/isLoggedIn";

	// User
	public static final String USER_ROOT = "user";
	public static final String CURRENT_USER = "";
	public static final String USER_FAVELISTS = "/favelists/{list}";
	public static final String USER_FAVEITEMS = "/favelists/{list}/items/{id}";
	public static final String EDIT_RANK = "/favelists/{list}/items/{id}/rank";
	public static final String EDIT_WHYLINE = "/favelists/{list}/items/{id}/whyline";
	public static final String USER_FOLLOWING = "/following/{user}";
	public static final String USER_SETTINGS = "/settings";
	public static final String CREATE_BLOBSTORE_URL = "/blobstore_url";
	public static final String PASSWORD_RESET = "/password/reset";
	public static final String PASSWORD_CHANGE = "/password/change";

	// Song
	public static final String SONG_ROOT = "songs";
	public static final String GET_SONG = "/{id}";
	public static final String GET_SONG_FAVELISTS = "/{id}/favelists";
	public static final String GET_SONG_WHYLINES = "/{id}/whylines";

	// Search
	public static final String SEARCH_ROOT = "search";
	public static final String SEARCH_FAVELISTS = "/favelists/{search_term}";
	public static final String GET_YOUTUBE_SEARCH_RESULTS = "/youtube";
	public static final String YOUTUBE_SEARCH_SONG_PARAM = "song";
	public static final String YOUTUBE_SEARCH_ARTIST_PARAM = "artist";

	// Trending
	public static final String TRENDING_ROOT = "trending";
	public static final String TRENDING_FAVELISTS = "/favelists";

}
