package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AlreadyFollowingException extends WebApplicationException {

	public AlreadyFollowingException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("You are already following that user").build());
	}

}
