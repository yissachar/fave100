package com.fave100.shared;

public class Constants {

	public static final String DEFAULT_HASHTAG = "alltime";
	public static final String SEARCH_SERVICE_URL = "http://default-environment-9n6mmewqkp.elasticbeanstalk.com";
	public static final String SEARCH_URL = SEARCH_SERVICE_URL + "/search?";
	public static final String LOOKUP_URL = SEARCH_SERVICE_URL + "/lookup?";
	public static final long MAX_AVATAR_SIZE = 1024 * 300; //300kb
	public static final int MAX_WHYLINE_LENGTH = 80;
	public static final int MAX_HASHTAG_LENGTH = 20;
	public static final int MAX_STARRED_LISTS = 50;
	public static final int MOBILE_WIDTH_PX = 768;
	public static final int MEDIUM_DISPLAY_WIDTH_PX = 1000;
	public static final int MORE_FOLLOWING_INC = 5;
	public static final int MAX_LISTS_PER_USER = 100;
	public static final int MAX_ITEMS_PER_LIST = 100;
	public static final int MAX_USERNAME_LENGTH = 15;
	public static final String API_PATH = "/api";
	public static final int TOP_BAR_HEIGHT = 55;
	public static final String TOP_BAR_HEIGHT_PX = TOP_BAR_HEIGHT + "px";
	public static final String FEATURED_LISTS_ID = "featured";
	public static final String SAW_WELCOME_INFO_STORAGE_KEY = "saw_welcome_info";
	public static final String TRENDING_LIST_NAME = "trending";
}
