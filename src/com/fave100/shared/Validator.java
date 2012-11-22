package com.fave100.shared;


public abstract class Validator {

	// TODO: Other validations?

	public static String validateUsername(final String username) {
		final String usernamePattern = "^[a-zA-Z0-9]+$";
		if(username == null || username.equals("")) {
			return "Username cannot be left empty";
		} else if(!username.matches(usernamePattern)) {
			return "Username must only consist of letters and numbers";
		}
		return null;
	}

	public static String validatePassword(final String password) {
		final int minPwdSize = 8;
		// TODO: force uppercase, lowercase, special char in pwd?
		if(password == null || password.equals("")) {
			return "Password cannot be left empty";
		} else if(password.length() < minPwdSize) {
			return "Password must be "+minPwdSize+" characters or longer";
		}
		return null;
	}

	public static String validateEmail(final String email) {
		final String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		if(email == null || email.equals("")) {
			return "Email cannot be left empty";
		} else if(!email.matches(emailPattern)) {
			return "Must be a valid email address";
		}
		return null;
	}

}
