package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CannotFollowYourselfException extends WebApplicationException {

	public CannotFollowYourselfException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("You cannot follow yourself").build());
	}

}
