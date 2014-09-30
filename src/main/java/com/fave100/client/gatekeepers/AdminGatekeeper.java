package com.fave100.client.gatekeepers;

import com.fave100.client.CurrentUser;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

public class AdminGatekeeper implements Gatekeeper {

	private CurrentUser _currentUser;

	@Inject
	public AdminGatekeeper(final CurrentUser currentUser) {
		_currentUser = currentUser;
	}

	@Override
	public boolean canReveal() {
		return _currentUser.isLoggedIn() && _currentUser.isAdmin();
	}

}
