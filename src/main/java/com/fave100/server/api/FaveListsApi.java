package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.favelist.FaveItemCollection;
import com.fave100.server.domain.favelist.Hashtag;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/" + ApiPaths.FAVELIST_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.FAVELIST_ROOT, description = "Operations on FaveLists")
public class FaveListsApi {

	@GET
	@Path(ApiPaths.GET_MASTER_FAVELIST)
	@ApiOperation(value = "Get a master FaveList", response = FaveItemCollection.class)
	public static FaveItemCollection getMasterFaveList(@ApiParam(value = "The list", required = true) @PathParam("list") final String list) {
		return new FaveItemCollection(ofy().load().type(Hashtag.class).id(list.toLowerCase()).now().getList());
	}
}
