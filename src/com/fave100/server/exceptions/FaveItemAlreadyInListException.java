package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FaveItemAlreadyInListException extends WebApplicationException {

	public FaveItemAlreadyInListException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("That item is already in the list").build());
	}

}
