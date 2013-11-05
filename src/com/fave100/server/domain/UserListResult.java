package com.fave100.server.domain;

public class UserListResult {

	private String _userName;
	private String _listName;
	private String _avatar;

	@SuppressWarnings("unused")
	private UserListResult() {
	};

	public UserListResult(String userName, String listName, String avatar) {
		setUserName(userName);
		setListName(listName);
		setAvatar(avatar);
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public String getListName() {
		return _listName;
	}

	public void setListName(String listName) {
		_listName = listName;
	}

	public String getAvatar() {
		return _avatar;
	}

	public void setAvatar(String avatar) {
		_avatar = avatar;
	}
}
