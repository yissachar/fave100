package com.fave100.client;

import com.fave100.client.pagefragments.unifiedsearch.SearchType;
import com.google.gwt.storage.client.Storage;
import com.google.inject.Inject;

public class StorageManager {

	private static final String SEARCH_TYPE = "searchType";
	private static final String ADD_MODE = "addMode";

	private Storage _storage;
	private CurrentUser _currentUser;

	@Inject
	public StorageManager(CurrentUser currentUser) {
		_currentUser = currentUser;
		_storage = Storage.getLocalStorageIfSupported();
	}

	private String userSpecificKey(String key) {
		return _currentUser.getUsername() + ":" + key;
	}

	public void setSearchType(SearchType type) {
		if (_storage == null || !_currentUser.isLoggedIn())
			return;

		_storage.setItem(userSpecificKey(SEARCH_TYPE), String.valueOf(type));
	}

	public SearchType getSearchType() {
		if (_storage == null || !_currentUser.isLoggedIn())
			return null;

		return _storage.getItem(userSpecificKey(SEARCH_TYPE)).equals("null") ? null : SearchType.valueOf(_storage.getItem(userSpecificKey(SEARCH_TYPE)));
	}

	public void setSearchAddMode(boolean addMode) {
		if (_storage == null || !_currentUser.isLoggedIn())
			return;

		_storage.setItem(userSpecificKey(ADD_MODE), String.valueOf(addMode));
	}

	public boolean isSearchAddMode() {
		if (_storage == null || !_currentUser.isLoggedIn())
			return false;

		return Boolean.valueOf(_storage.getItem(userSpecificKey(ADD_MODE)));
	}

}
