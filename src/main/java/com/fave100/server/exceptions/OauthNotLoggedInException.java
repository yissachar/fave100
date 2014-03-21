package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class OauthNotLoggedInException extends WebApplicationException {

	public OauthNotLoggedInException(String provider) {
		super(Response.status(Response.Status.UNAUTHORIZED).entity("You are not logged into your " + provider + " account").build());
	}

}
