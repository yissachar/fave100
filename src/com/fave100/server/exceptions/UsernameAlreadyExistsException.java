package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class UsernameAlreadyExistsException extends WebApplicationException {

	public UsernameAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("A user with that name already exists").build());
	}

}
