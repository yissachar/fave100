package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class TwitterIdAlreadyExistsException extends WebApplicationException {

	public TwitterIdAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("An account is already associated with that Twitter Id").build());
	}

}
