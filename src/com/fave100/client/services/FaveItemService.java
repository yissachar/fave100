package com.fave100.client.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.fave100.shared.api.ApiPaths;
import com.fave100.shared.domain.FaveItemCollection;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.rest.RestService;

@Path("/fave100/v1/")
public interface FaveItemService extends RestService {

	@GET
	@Path(ApiPaths.GET_MASTER_LIST)
	public Action<FaveItemCollection> getMasterFaveList(String listName);
}
