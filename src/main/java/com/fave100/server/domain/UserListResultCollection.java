package com.fave100.server.domain;

import java.util.List;

public class UserListResultCollection {

	private List<UserListResult> items;

	public UserListResultCollection(List<UserListResult> items) {
		this.setItems(items);
	}

	public List<UserListResult> getItems() {
		return items;
	}

	public void setItems(List<UserListResult> items) {
		this.items = items;
	}

}
