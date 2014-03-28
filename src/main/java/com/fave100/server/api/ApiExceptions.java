package com.fave100.server.api;

import com.fave100.shared.Constants;

public class ApiExceptions {

	public static final String USER_NOT_FOUND = "User not found";
	public static final String FAVELIST_NOT_FOUND = "FaveList not found";
	public static final String USERNAME_ALREADY_EXISTS = "A user with that name already exists";
	public static final String EMAIL_ID_ALREADY_EXISTS = "A user with that email address already exist";
	public static final String NOT_LOGGED_IN = "You are not logged in";
	public static final String FAVELIST_LIMIT_REACHED = "You can't have more than " + Constants.MAX_LISTS_PER_USER + " lists";
	public static final String FAVELIST_SIZE_REACHED = "You can't have more than " + Constants.MAX_ITEMS_PER_LIST + " items in a list";
	public static final String FAVELIST_ALREADY_EXISTS = "You already have a list with that name";
	public static final String FAVEITEM_ALREADY_IN_LIST = "That item is already in the list";
	public static final String INVALID_FAVELIST_INDEX = "Index out of range";
	public static final String DID_NOT_PASS_VALIDATION = "Did not pass validation";
}
