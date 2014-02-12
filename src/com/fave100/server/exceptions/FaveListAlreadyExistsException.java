package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FaveListAlreadyExistsException extends WebApplicationException {

	public FaveListAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("You already have a list with that name").build());
	}

}
