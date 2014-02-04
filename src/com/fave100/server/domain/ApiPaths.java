package com.fave100.server.domain;

public class ApiPaths {

	public static final String API_NAME = "fave100";
	public static final String API_VERSION = "v1";

	// FaveList paths
	public static final String FAVELIST_ROOT = "favelist";
	public static final String GET_FAVELIST = "/getFavelist";
	public static final String GET_MASTER_FAVELIST = "/masterFaveList";
	public static final String GET_HASHTAG_AUTOCOMPLETE = "/hashtagAutocomplete";
	public static final String TRENDING_FAVELISTS = "/trendingFaveLists";
	public static final String GET_LISTS_CONTAINING_SONG = "/getListsContainingSong";
	public static final String ADD_FAVELIST = "/add";
	public static final String DELETE_FAVELIST = "/delete";

	// AppUser paths
	public static final String APPUSER_ROOT = "appuser";
	public static final String LOGIN = "/login";
	public static final String LOGGED_IN_APPUSER = "/loggedInAppUser";

}
