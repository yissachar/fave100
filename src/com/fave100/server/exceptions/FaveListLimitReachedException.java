package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.shared.Constants;

public class FaveListLimitReachedException extends WebApplicationException {

	public FaveListLimitReachedException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("You can't have more than " + Constants.MAX_LISTS_PER_USER + " lists").build());
	}

}
