package com.fave100.server;

import javax.servlet.http.HttpServletRequest;

/*
 * A convenience class for the server to build URLs similar to ParameterTokenFormatter/PlaceRequest 
 * on GWTP client.
 */
public class UrlBuilder {

	private StringBuilder placeTokenBuilder = new StringBuilder();

	public UrlBuilder(final String placeToken, HttpServletRequest req) {
		placeTokenBuilder.append(req.getScheme());
		placeTokenBuilder.append("://");
		placeTokenBuilder.append(req.getServerName());

		if (req.getServerPort() != 80 && req.getServerPort() != 443) {
			placeTokenBuilder.append(":");
			placeTokenBuilder.append(req.getServerPort());
		}

		placeTokenBuilder.append("/");
		if (!placeToken.isEmpty()) {
			placeTokenBuilder.append("#");
			placeTokenBuilder.append(placeToken);
		}
	}

	public UrlBuilder with(final String param, final String arg) {
		placeTokenBuilder.append(";");
		placeTokenBuilder.append(param);
		placeTokenBuilder.append("=");
		placeTokenBuilder.append(arg);
		return this;
	}

	public String build() {
		return placeTokenBuilder.toString();
	}

}