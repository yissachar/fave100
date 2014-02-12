package com.fave100.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.favelist.FaveListDao;

public class FaveListSizeReachedException extends WebApplicationException {

	public FaveListSizeReachedException() {
		super(Response.status(Response.Status.FORBIDDEN).entity("You cannot have more than " + FaveListDao.MAX_FAVES + " items in a list").build());
	}

}
