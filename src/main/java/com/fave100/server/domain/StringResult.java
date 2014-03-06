package com.fave100.server.domain;

/**
 * A wrapper class for Booleans for use in Google Cloud Endpoints, since GCE cannot handle
 * primitive types directly.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class StringResult {

	private String value;

	public StringResult(String value) {
		setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
