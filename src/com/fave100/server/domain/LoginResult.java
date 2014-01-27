package com.fave100.server.domain;

import com.fave100.server.domain.appuser.AppUser;

public class LoginResult {

	private AppUser appUser;

	private String sessionId;

	public LoginResult(AppUser appUser, String sessionId) {
		this.appUser = appUser;
		this.sessionId = sessionId;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
