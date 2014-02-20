package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

public class FaveListLimitReachedException extends WebApplicationException {

	public FaveListLimitReachedException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.FAVELIST_LIMIT_REACHED).build());
	}

}
