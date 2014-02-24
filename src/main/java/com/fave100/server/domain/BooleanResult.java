package com.fave100.server.domain;

/**
 * A wrapper class for Booleans for use in Google Cloud Endpoints, since GCE cannot handle
 * primitive types directly.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class BooleanResult {

	private boolean value;

	public BooleanResult(boolean value) {
		setValue(value);
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
