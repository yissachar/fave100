package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GoogleIdAlreadyExistsException extends WebApplicationException {

	public GoogleIdAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("An account is already associated with that GoogleID").build());
	}

}