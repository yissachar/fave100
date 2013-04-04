package com.fave100.shared;

import com.google.gwt.core.shared.GWT;

public class UrlBuilder {

	private String url;

	public UrlBuilder(final String placeToken) {
		url = "";
		if(GWT.isProdMode()) {
			url += "http://fave-100.appspot.com/";
		} else {
			url += "http://yissachar:8888/Fave100.html?gwt.codesvr=127.0.0.1:9997";
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
