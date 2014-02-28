package com.fave100.server.domain;

public class ApiPaths {

	// FaveList
	public static final String FAVELIST_ROOT = "favelist";
	public static final String GET_HASHTAG_AUTOCOMPLETE = "/hashtagAutocomplete";
	public static final String TRENDING_FAVELISTS = "/trendingFaveLists";
	public static final String GET_LISTS_CONTAINING_SONG = "/getListsContainingSong";
	public static final String ADD_FAVELIST = "/add";
	public static final String DELETE_FAVELIST = "/delete";
	public static final String ADD_FAVEITEM = "/item/add";
	public static final String REMOVE_FAVEITEM = "/item/remove";
	public static final String EDIT_WHYLINE = "/item/whyline/edit";

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
	public static final String GET_GOOGLE_AUTH_URL = "/google/authUrl";
	public static final String GET_FACEBOOK_AUTH_URL = "/facebook/authUrl";
	public static final String GET_TWITTER_AUTH_URL = "/twitter/authUrl";

	// AppUser
	public static final String APPUSER_ROOT = "user";
	public static final String GET_APPUSER = "/{username}";
	public static final String GET_FOLLOWING = "/following";
	public static final String IS_FOLLOWING = "/isFollowing";
	public static final String IS_GOOGLE_LOGGED_IN = "/google/loggedin";
	public static final String IS_APPUSER_LOGGED_IN = "/isLoggedIn";
	public static final String FOLLOW = "/follow";
	public static final String UNFOLLOW = "/unfollow";

	// Account
	public static final String ACCOUNT_ROOT = "account";
	public static final String ACCOUNT_SETTINGS = "/settings";
	public static final String CURRENT_USER = "/me";
	public static final String CREATE_BLOBSTORE_URL = "/blobstore_url";
	public static final String PASSWORD_RESET = "/password/reset";
	public static final String PASSWORD_CHANGE = "/password/change";

	// Song
	public static final String SONG_ROOT = "song";
	public static final String GET_YOUTUBE_SEARCH_RESULTS = "/youtubeSearchResults";
	public static final String YOUTUBE_SEARCH_SONG_PARAM = "song";
	public static final String YOUTUBE_SEARCH_ARTIST_PARAM = "artist";

	// Whyline
	public static final String WHYLINE_ROOT = "whyline";
	public static final String GET_SONG_WHYLINES = "/song/{id}";

}
