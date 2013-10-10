package com.fave100.shared;

public abstract class Validator {

	public static String validateUsername(final String username) {
		final String usernamePattern = "^[a-zA-Z0-9]+$";
		final int maxNameLength = 15;
		if (username == null || username.equals("")) {
			return "Username cannot be left empty";
		}
		else if (username.length() > maxNameLength) {
			return "Username must be less than " + maxNameLength + " characters long";
		}
		else if (!username.matches(usernamePattern)) {
			return "Username must only consist of letters and numbers";
		}
		return null;
	}

	public static String validatePassword(final String password) {
		final int minPwdSize = 6;
		if (password == null || password.equals("")) {
			return "Password cannot be left empty";
		}
		else if (password.length() < minPwdSize) {
			return "Password must be " + minPwdSize + " characters or longer";
		}
		return null;
	}

	public static String validateEmail(final String email) {
		final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		if (email == null || email.equals("")) {
			return "Email cannot be left empty";
		}
		else if (!email.matches(emailPattern)) {
			return "Must be a valid email address";
		}
		return null;
	}

	public static String validateWhyline(final String whyline) {
		if (whyline.length() > Constants.MAX_WHYLINE_LENGTH) {
			return "Whyline must be " + Constants.MAX_WHYLINE_LENGTH + " characters or less";
		}
		return null;
	}

	public static String validateHashtag(final String hashtag) {
		final String hashtagPattern = "^[a-zA-Z0-9]+$";
		if (hashtag == null || hashtag.equals("")) {
			return "List name cannot be left empty";
		}
		if (hashtag.length() > Constants.MAX_HASHTAG_LENGTH) {
			return "List name must be " + Constants.MAX_HASHTAG_LENGTH + " characters or less";
		}
		else if (!hashtag.matches(hashtagPattern)) {
			if (hashtag.contains(" "))
				return "List name must not contain spaces";
			return "List name must only consist of letters and numbers";
		}
		return null;
	}

}
