package com.fave100.server.domain;

public class PasswordChangeDetails {

	private String newPassword;
	private String tokenOrPassword;

	public PasswordChangeDetails() {
	}

	public PasswordChangeDetails(String newPassword, String tokenOrPassword) {
		this.newPassword = newPassword;
		this.tokenOrPassword = tokenOrPassword;
	}

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
