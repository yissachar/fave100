package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

public class FaveListSizeReachedException extends WebApplicationException {

	public FaveListSizeReachedException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.FAVELIST_SIZE_REACHED).build());
	}

}
