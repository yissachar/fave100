package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.api.ApiExceptions;

@SuppressWarnings("serial")
public class FaveItemAlreadyInListException extends WebApplicationException {

	public FaveItemAlreadyInListException() {
		super(Response.status(Response.Status.FORBIDDEN).entity(ApiExceptions.FAVEITEM_ALREADY_IN_LIST).build());
	}

}
