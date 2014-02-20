package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

public class EmailIdAlreadyExistsException extends WebApplicationException {

	public EmailIdAlreadyExistsException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.EMAIL_ID_ALREADY_EXISTS).build());
	}

}
