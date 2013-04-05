package com.fave100.shared;


public class UrlBuilder {

	// Up to the client to set this to true on init
	public static boolean isDevMode = false;

	private String url;

	public UrlBuilder(final String placeToken) {
		url = "";
		if(isDevMode) {
			url += "http://yissachar:8888/Fave100.html?gwt.codesvr=127.0.0.1:9997";
		} else {
			url += "http://fave-100.appspot.com/";
		}

		url += "#";
		url += placeToken;
	}

	public UrlBuilder with(final String param, final String arg) {
		url += ";" + param + "=" + arg;
		return this;
	}

	/* Getters and Setters */

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

}
