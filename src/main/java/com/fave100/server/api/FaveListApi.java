package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.favelist.FaveItemCollection;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.FAVELIST_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.FAVELIST_ROOT, description = "Operations on FaveLists")
public class FaveListApi {

	@GET
	@Path("/{username}/{list}")
	@ApiOperation(value = "Get a user's FaveList", response = FaveItemCollection.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = ApiExceptions.FAVELIST_NOT_FOUND)})
	public static FaveItemCollection getFaveList(
			@ApiParam(value = "The username", required = true) @PathParam("username") final String username,
			@ApiParam(value = "The list", required = true) @PathParam("list") final String list) {

		final FaveList faveList = FaveListDao.findFaveList(username, list);
		if (faveList == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(ApiExceptions.FAVELIST_NOT_FOUND).build());

		return new FaveItemCollection(faveList.getList());
	}

	@GET
	@Path("/{list}")
	@ApiOperation(value = "Get a master FaveList", response = FaveItemCollection.class)
	public static FaveItemCollection getMasterFaveList(@ApiParam(value = "The list", required = true) @PathParam("list") final String list) {
		return new FaveItemCollection(ofy().load().type(Hashtag.class).id(list.toLowerCase()).get().getList());
	}
}
