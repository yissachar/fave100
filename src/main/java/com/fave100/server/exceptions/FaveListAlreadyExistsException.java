package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

@SuppressWarnings("serial")
public class FaveListAlreadyExistsException extends WebApplicationException {

	public FaveListAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.FAVELIST_ALREADY_EXISTS).build());
	}

}
