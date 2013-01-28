package com.fave100.client;

import com.fave100.client.requestfactory.AppUserProxy;

public class CurrentUser {

	private AppUserProxy appUser;

	public CurrentUser() {
	}

	public AppUserProxy getAppUser() {
		return appUser;
	}

	public void setAppUser(final AppUserProxy appUser) {
		this.appUser = appUser;
	}

}
