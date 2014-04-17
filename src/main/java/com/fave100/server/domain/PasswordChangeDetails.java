package com.fave100.server.domain;

public class PasswordChangeDetails {

	private String newPassword;
	private String tokenOrPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getTokenOrPassword() {
		return tokenOrPassword;
	}

	public void setTokenOrPassword(String tokenOrPassword) {
		this.tokenOrPassword = tokenOrPassword;
	}

}
