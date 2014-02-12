package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class NotLoggedInException extends WebApplicationException {

	public NotLoggedInException() {
		super(Response.status(Response.Status.UNAUTHORIZED).entity("You are not logged in").build());
	}

}
