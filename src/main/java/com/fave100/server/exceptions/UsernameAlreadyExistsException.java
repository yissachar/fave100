package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

@SuppressWarnings("serial")
public class UsernameAlreadyExistsException extends WebApplicationException {

	public UsernameAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.USERNAME_ALREADY_EXISTS).build());
	}

}
