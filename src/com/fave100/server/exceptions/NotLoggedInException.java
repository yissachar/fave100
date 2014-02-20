package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

public class NotLoggedInException extends WebApplicationException {

	public NotLoggedInException() {
		super(Response.status(Response.Status.UNAUTHORIZED).entity(ApiExceptions.NOT_LOGGED_IN).build());
	}

}
