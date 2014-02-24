package com.fave100.server.domain;

public class ApiPaths {

	// FaveList paths
	public static final String FAVELIST_ROOT = "favelist";
	public static final String GET_HASHTAG_AUTOCOMPLETE = "/hashtagAutocomplete";
	public static final String TRENDING_FAVELISTS = "/trendingFaveLists";
	public static final String GET_LISTS_CONTAINING_SONG = "/getListsContainingSong";
	public static final String ADD_FAVELIST = "/add";
	public static final String DELETE_FAVELIST = "/delete";
	public static final String ADD_FAVEITEM = "/item/add";
	public static final String REMOVE_FAVEITEM = "/item/remove";
	public static final String EDIT_WHYLINE = "/item/whyline/edit";

	// AppUser paths
	public static final String APPUSER_ROOT = "appuser";
	public static final String GET_APPUSER = "/getAppUser";
	public static final String CREATE_APPUSER = "/createAppUser";
	public static final String CREATE_APPUSER_FROM_GOOGLE_ACCOUNT = "/createAppUserFromGoogleAccount";
	public static final String CREATE_APPUSER_FROM_TWITTER_ACCOUNT = "/createAppUserFromTwitterAccount";
	public static final String CREATE_APPUSER_FROM_FACEBOOK_ACCOUNT = "/createAppUserFromFacebookAccount";
	public static final String LOGIN = "/login";
	public static final String LOGIN_WITH_GOOGLE = "/loginWithGoogle";
	public static final String LOGIN_WITH_TWITTER = "/loginWithTwitter";
	public static final String LOGIN_WITH_FACEBOOK = "/loginWithFacebook";
	public static final String LOGOUT = "/logout";
	public static final String LOGGED_IN_APPUSER = "/loggedInAppUser";
	public static final String GET_FOLLOWING = "/following";
	public static final String IS_FOLLOWING = "/isFollowing";
	public static final String IS_GOOGLE_LOGGED_IN = "/google/loggedin";
	public static final String GET_GOOGLE_LOGIN_URL = "/google/loginUrl";
	public static final String GET_FACEBOOK_AUTH_URL = "/facebook/authUrl";
	public static final String GET_TWITTER_AUTH_URL = "/twitter/authUrl";
	public static final String IS_APPUSER_LOGGED_IN = "/isLoggedIn";
	public static final String CREATE_BLOBSTORE_URL = "/createBlobstoreUrl";;
	public static final String USER_SETTINGS = "/settings";
	public static final String FOLLOW = "/follow";
	public static final String UNFOLLOW = "/unfollow";
	public static final String EMAIL_PASSWORD_RESET = "/password/reset";
	public static final String CHANGE_PASSWORD = "/password/change";

	// Song paths
	public static final String SONG_ROOT = "song";
	public static final String GET_YOUTUBE_SEARCH_RESULTS = "/youtubeSearchResults";
	public static final String YOUTUBE_SEARCH_SONG_PARAM = "song";
	public static final String YOUTUBE_SEARCH_ARTIST_PARAM = "artist";

	// Whyline paths
	public static final String WHYLINE_ROOT = "whyline";
	public static final String GET_SONG_WHYLINES = "/song/{id}";

}
