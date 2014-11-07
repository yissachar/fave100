package com.fave100.server.domain;

public final class ApiPaths {

	// Prevent instantiation
	private ApiPaths() {
	}

	// FaveList
	public static final String FAVELIST_ROOT = "favelists";
	public static final String GET_LIST_NAMES = "/names";
	public static final String GET_MASTER_FAVELIST = "/list/{list}";
	public static final String MASTER_FAVELIST_MODES = GET_MASTER_FAVELIST + "/modes";
	public static final String FEATURED_FAVELISTS = "/featured";
	public static final String EDIT_FEATURED_FAVELISTS = "/featured/{list}";

	// Auth
	public static final String AUTH_ROOT = "auth";
	public static final String REGISTER = "/register";
	public static final String CREATE_APPUSER_FROM_GOOGLE_ACCOUNT = "/google/register";
	public static final String CREATE_APPUSER_FROM_TWITTER_ACCOUNT = "/twitter/register";
	public static final String CREATE_APPUSER_FROM_FACEBOOK_ACCOUNT = "/facebook/register";
	public static final String LOGIN = "/login";
	public static final String LOGIN_WITH_GOOGLE = "/google/login";
	public static final String LOGIN_WITH_TWITTER = "/twitter/login";
	public static final String LOGIN_WITH_FACEBOOK = "/facebook/login";
	public static final String LOGOUT = "/logout";
	public static final String GET_GOOGLE_AUTH_URL = "/google/url";
	public static final String GET_FACEBOOK_AUTH_URL = "/facebook/url";
	public static final String GET_TWITTER_AUTH_URL = "/twitter/url";

	// Users
	public static final String USERS_ROOT = "users";
	public static final String GET_USER = "/{user}";
	public static final String ADMINS = "/admins";
	public static final String CRITICS = "/critics";
	public static final String ALTER_ADMIN = "/admins/{user}";
	public static final String ALTER_CRITIC = "/critics/{user}";
	public static final String GET_USERS_FAVELIST = "/{user}/favelists/{list}";
	public static final String LIST_CRITIC_URL = "/{user}/favelists/{list}/critic_url";
	public static final String GET_USERS_FOLLOWING = "/{user}/following";
	public static final String IS_APPUSER_LOGGED_IN = "/isLoggedIn";

	// User
	public static final String USER_ROOT = "user";
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
	public static final String SEARCH_FAVELISTS = "/favelists";
	public static final String SEARCH_USERS = "/users";
	public static final String GET_YOUTUBE_SEARCH_RESULTS = "/youtube";
	public static final String YOUTUBE_SEARCH_SONG_PARAM = "song";
	public static final String YOUTUBE_SEARCH_ARTIST_PARAM = "artist";

	// Trending
	public static final String TRENDING_ROOT = "trending";
	public static final String TRENDING_FAVELISTS = "/favelists";

}
