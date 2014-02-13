package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class EmailIdAlreadyExistsException extends WebApplicationException {

	public EmailIdAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("A user with that email address already exists").build());
	}

}
