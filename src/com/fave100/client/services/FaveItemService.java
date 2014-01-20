package com.fave100.client.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fave100.shared.domain.FaveItemCollection;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path("/fave100/v1")
public interface FaveItemService extends RestService {

	@GET
	@Path("/faveitem")
	public Action<FaveItemCollection> listFaveItems();
}
