package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.MemcacheManager;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.FeaturedLists;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItemCollection;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.Constants;
import com.fave100.shared.ListMode;
import com.sun.jersey.api.NotFoundException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.FAVELIST_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.FAVELIST_ROOT, description = "Operations on FaveLists")
public class FaveListsApi {

	public static final int QUALITY_LIST_SIZE = 10;

	@GET
	@Path(ApiPaths.GET_LIST_NAMES)
	@ApiOperation(value = "Get FaveList names", response = StringResultCollection.class)
	public static StringResultCollection getListNames() {
		List<Hashtag> masterLists = ofy().load().type(Hashtag.class).limit(1000).list();

		List<StringResult> listNames = new ArrayList<>();
		for (Hashtag masterList : masterLists) {
			// Only add if the list has enough picks to qualify as a "quality" list
			if (masterList.getList().size() >= QUALITY_LIST_SIZE) {
				listNames.add(new StringResult(masterList.getName()));
			}
		}

		return new StringResultCollection(listNames);
	}

	@GET
	@Path(ApiPaths.GET_MASTER_FAVELIST)
	@ApiOperation(value = "Get a master FaveList", response = FaveItemCollection.class)
	public static FaveItemCollection getMasterFaveList(@PathParam("list") final String list, @QueryParam("mode") @DefaultValue("all") String mode) {
		String listName = list.toLowerCase();

		// Attempt to get the list from memcache first, if possible
		if (ListMode.NEWEST.equals(mode)) {
			return new FaveItemCollection(MemcacheManager.getNewestSongs(listName));
		}

		Hashtag masterList = ofy().load().type(Hashtag.class).id(listName).now();
		if (masterList == null)
			throw new NotFoundException();

		if (ListMode.USERS.equals(mode)) {
			return new FaveItemCollection(masterList.getList());
		}
		else if (ListMode.CRITICS.equals(mode)) {
			return new FaveItemCollection(masterList.getCriticsList());
		}
		else if (ListMode.NEWEST.equals(mode)) {
			return new FaveItemCollection(masterList.getNewestList());
		}

		throw new NotFoundException();
	}

	@GET
	@Path(ApiPaths.MASTER_FAVELIST_MODES)
	@ApiOperation(value = "Returns the modes that exist for the list", response = StringResultCollection.class)
	public static StringResultCollection getMasterFaveListModes(@PathParam("list") final String list) {
		String listName = list.toLowerCase();

		Hashtag masterList = ofy().load().type(Hashtag.class).id(listName).now();
		if (masterList == null)
			throw new NotFoundException();

		List<String> modes = new ArrayList<String>();
		if (!masterList.getList().isEmpty()) {
			modes.add(ListMode.USERS);
		}

		if (!masterList.getCriticsList().isEmpty()) {
			modes.add(ListMode.CRITICS);
		}

		if (!masterList.getNewestList().isEmpty()) {
			modes.add(ListMode.NEWEST);
		}

		List<StringResult> items = new ArrayList<StringResult>();
		for (String mode : modes) {
			items.add(new StringResult(mode));
		}

		return new StringResultCollection(items);
	}

	@GET
	@Path(ApiPaths.FEATURED_FAVELISTS)
	@ApiOperation(value = "Get the featured FaveLists", response = FeaturedLists.class)
	public static FeaturedLists getFeaturedLists(@LoggedInUser AppUser currentUser) {
		if (!currentUser.isAdmin())
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

		FeaturedLists featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now();
		if (featuredLists == null)
			throw new NotFoundException();

		return featuredLists;
	}

	@POST
	@Path(ApiPaths.EDIT_FEATURED_FAVELISTS)
	@ApiOperation(value = "Add a featured FaveList")
	public static void addFeaturedList(@LoggedInUser AppUser currentUser, @PathParam("list") final String list) {
		if (!currentUser.isAdmin())
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

		FeaturedLists featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now();
		if (featuredLists == null) {
			featuredLists = new FeaturedLists(Constants.FEATURED_LISTS_ID);
		}

		featuredLists.getLists().add(list);
		ofy().save().entity(featuredLists).now();
	}

	@DELETE
	@Path(ApiPaths.EDIT_FEATURED_FAVELISTS)
	@ApiOperation(value = "Remove a featured FaveList")
	public static void removeFeaturedList(@LoggedInUser AppUser currentUser, @PathParam("list") final String list) {
		if (!currentUser.isAdmin())
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

		FeaturedLists featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now();
		if (featuredLists == null)
			throw new NotFoundException();

		featuredLists.getLists().remove(list);
		ofy().save().entity(featuredLists).now();
	}

	@POST
	@Path(ApiPaths.FEATURED_FAVELISTS)
	@ApiOperation(value = "Sets featured FaveLists to be random or not")
	public static void setFeaturedFavelistsRandomized(@LoggedInUser AppUser currentUser, BooleanResult randomized) {
		if (!currentUser.isAdmin())
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

		FeaturedLists featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now();
		if (featuredLists == null)
			throw new NotFoundException();

		featuredLists.setRandomized(randomized.getValue());
		ofy().save().entity(featuredLists).now();
	}

}
