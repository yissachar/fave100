package com.fave100.server;

/*
 * A convenience class for the server to build URLs similar to ParameterTokenFormatter/PlaceRequest 
 * on GWTP client.
 */
public class UrlBuilder {

	// Up to the server to set this to true on init
	public static boolean isDevMode = false;

	// The entire URL including place
	private String url;
	// Just the place
	private String placeToken;

	public UrlBuilder(final String placeToken) {
		url = "";
		if (isDevMode) {
			url += "http://yissachar:8888/Fave100.html?gwt.codesvr=127.0.0.1:9997";
		}
		else {
			url += "http://www.fave100.com/";
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
