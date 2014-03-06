package com.fave100.client.gatekeepers;

import com.fave100.client.CurrentUser;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

public class LoggedInGatekeeper implements Gatekeeper {

	private CurrentUser currentUser;

	@Inject
	public LoggedInGatekeeper(final CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public boolean canReveal() {
		return currentUser.isLoggedIn();
	}

}
