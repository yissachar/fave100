package com.fave100.shared;


public class UrlBuilder {

	// Up to the client to set this to true on init
	public static boolean isDevMode = false;

	// The entire URL including place
	private String url;
	// Just the place
	private String placeToken;

	public UrlBuilder(final String placeToken) {
		url = "";
		if(isDevMode) {
			url += "http://yissachar:8888/Fave100.html?gwt.codesvr=127.0.0.1:9997";
		} else {
			url += "http://fave-100.appspot.com/";
		}

		this.placeToken = placeToken;
	}

	public UrlBuilder with(final String param, final String arg) {
		placeToken += ";" + param + "=" + arg;
		return this;
	}

	/* Getters and Setters */

	public String getUrl() {
		return url + "#" + placeToken;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getPlaceToken() {
		return placeToken;
	}

	public void setPlaceToken(final String placeToken) {
		this.placeToken = placeToken;
	}

}
