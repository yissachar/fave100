package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class InvalidLoginException extends WebApplicationException {

	public InvalidLoginException() {
		super(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build());
	}

}
