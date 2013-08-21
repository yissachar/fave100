package com.fave100.shared.exceptions.favelist;

@SuppressWarnings("serial")
public class FaveListAlreadyExistsException extends Exception {

	public FaveListAlreadyExistsException(final String msg) {
		super(msg);
	}

}
