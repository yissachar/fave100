package com.fave100.server.domain.appuser;

import java.util.List;

public class AppUserCollection {

	private List<AppUser> items;

	public AppUserCollection(List<AppUser> items) {
		this.setItems(items);
	}

	public List<AppUser> getItems() {
		return items;
	}

	public void setItems(List<AppUser> items) {
		this.items = items;
	}

}
