package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveItemCollection;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.server.exceptions.FaveItemAlreadyInListException;
import com.fave100.server.exceptions.FaveListAlreadyExistsException;
import com.fave100.server.exceptions.FaveListLimitReachedException;
import com.fave100.server.exceptions.FaveListSizeReachedException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
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

	@GET
	@Path(ApiPaths.GET_HASHTAG_AUTOCOMPLETE)
	@ApiOperation(value = "Get list name suggestions", response = StringResultCollection.class)
	public static StringResultCollection getHashtagAutocomplete(@ApiParam(value = "The search term", required = true) @QueryParam("searchTerm") final String searchTerm) {

		final List<StringResult> names = new ArrayList<>();

		if (searchTerm.isEmpty())
			return new StringResultCollection(names);

		// TODO: Need to sort by popularity
		final List<Hashtag> hashtags = ofy().load().type(Hashtag.class).filter("id >=", searchTerm.toLowerCase()).filter("id <", searchTerm.toLowerCase() + "\uFFFD").limit(5).list();
		for (final Hashtag hashtag : hashtags) {
			names.add(new StringResult(hashtag.getName()));
		}

		return new StringResultCollection(names);
	}

	@GET
	@Path(ApiPaths.TRENDING_FAVELISTS)
	@ApiOperation(value = "Get a list of trending FaveLists", response = StringResultCollection.class)
	public static StringResultCollection getTrendingFaveLists() {
		// Nov 26 2013: Temporarily disabling proper trending in favor of hard-coded popular lists
		//		List<Hashtag> hashtags = ofy().load().type(Hashtag.class).order("-zscore").limit(5).list();
		//		List<String> trending = new ArrayList<>();
		//		for (Hashtag hashtag : hashtags) {
		//			trending.add(hashtag.getName());
		//		}
		//		return trending;
		List<StringResult> trending = new ArrayList<>();
		trending.add(new StringResult("alltime"));
		trending.add(new StringResult("2014"));
		trending.add(new StringResult("2013"));
		trending.add(new StringResult("2012"));
		trending.add(new StringResult("dance"));
		trending.add(new StringResult("classicrock"));
		return new StringResultCollection(trending);
	}

	@POST
	@Path(ApiPaths.ADD_FAVELIST)
	@ApiOperation(value = "Add a FaveList")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "The list name did not pass validation"), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN),
							@ApiResponse(code = 403, message = ApiExceptions.FAVELIST_LIMIT_REACHED), @ApiResponse(code = 403, message = ApiExceptions.FAVELIST_ALREADY_EXISTS)})
	public static void addFaveListForCurrentUser(@Context HttpServletRequest request, @ApiParam(value = "The list name", required = true) @QueryParam("listName") final String listName) {

		final String error = Validator.validateHashtag(listName);
		if (error != null) {
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
		}

		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		// -1 because #alltime is a default list not stored in the hashtags list
		if (currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER - 1)
			throw new FaveListLimitReachedException();

		final String username = currentUser.getUsername();

		if (FaveListDao.findFaveList(username, listName) != null)
			throw new FaveListAlreadyExistsException();

		currentUser.getHashtags().add(listName);
		final FaveList faveList = new FaveList(username, listName);
		// Transaction to ensure no duplicate hashtags created
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				Hashtag hashtag = ofy().load().type(Hashtag.class).id(listName).get();
				// Hashtag already exists, add it to user's lists
				if (hashtag != null) {
					ofy().save().entities(currentUser, faveList).now();
				}
				// Create a new hashtag
				else {
					hashtag = new Hashtag(listName, username);
					ofy().save().entities(currentUser, faveList, hashtag).now();
				}
			}
		});

		return;
	}

	@DELETE
	@Path(ApiPaths.DELETE_FAVELIST)
	@ApiOperation(value = "Delete a FaveList")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void deleteFaveListForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String listName) {
		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		currentUser.getHashtags().remove(listName);
		ofy().save().entity(currentUser).now();

		FaveList listToDelete = ofy().load().type(FaveList.class).id(currentUser.getUsername().toLowerCase() + FaveListDao.SEPERATOR_TOKEN + listName.toLowerCase()).get();

		// Get associated WhyLines and mark for deletion
		List<Ref<Whyline>> whylinesToDelete = new ArrayList<>();
		for (FaveItem faveItem : listToDelete.getList()) {
			Ref<Whyline> whylineRef = faveItem.getWhylineRef();
			if (whylineRef != null)
				whylinesToDelete.add(whylineRef);
		}

		// Delete FaveList
		ofy().delete().entity(listToDelete).now();

		// Delete associated WhyLines
		ofy().delete().entities(whylinesToDelete).now();

		return;
	}

	@POST
	@Path(ApiPaths.ADD_FAVEITEM)
	@ApiOperation(value = "Add a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN), @ApiResponse(code = 403, message = ApiExceptions.FAVELIST_SIZE_REACHED),
							@ApiResponse(code = 403, message = ApiExceptions.FAVEITEM_ALREADY_IN_LIST)})
	public static void addFaveItemForCurrentUser(@Context HttpServletRequest request, @QueryParam("listName") final String listName, @QueryParam("songId") final String songID) {

		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), listName);

		// Check FaveList size limit reached
		if (faveList.getList().size() >= FaveListDao.MAX_FAVES)
			throw new FaveListSizeReachedException();

		// Get the song from Lucene lookup
		final FaveItem newFaveItem = SongApi.getSong(songID);
		if (newFaveItem == null)
			return;

		// Check if it is a unique song for this user
		boolean unique = true;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(newFaveItem.getSongID())) {
				unique = false;
			}
		}

		// Check if FaveItem is already is in the list
		if (!unique)
			throw new FaveItemAlreadyInListException();

		// Create the new FaveItem
		faveList.getList().add(newFaveItem);
		ofy().save().entities(faveList).now();

		return;
	}

	@DELETE
	@Path(ApiPaths.REMOVE_FAVEITEM)
	@ApiOperation(value = "Remove a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void removeFaveItemForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String hashtag, @QueryParam("songId") final String songID) {

		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Find the song to remove
		FaveItem faveItemToRemove = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songID)) {
				faveItemToRemove = faveItem;
				break;
			}
		}

		if (faveItemToRemove == null)
			return;

		// We must also delete the whyline if it exists
		final Ref<Whyline> currentWhyline = faveItemToRemove.getWhylineRef();
		if (currentWhyline != null) {
			ofy().delete().key(currentWhyline.getKey()).now();
		}
		faveList.getList().remove(faveItemToRemove);
		ofy().save().entities(faveList).now();

		return;
	}

	@POST
	@Path("/rerank/{list}/{songId}/{newIndex}")
	@ApiOperation(value = "Rerank a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 400, message = ApiExceptions.INVALID_FAVELIST_INDEX), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void rerankFaveItemForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String hashtag, @PathParam("songId") final String songID,
			@PathParam("newIndex") final int newIndex) {

		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Make sure new index is valid
		if (newIndex < 0 || newIndex >= faveList.getList().size())
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ApiExceptions.INVALID_FAVELIST_INDEX).build());

		// Find the song to change position
		FaveItem faveItemToRerank = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songID)) {
				faveItemToRerank = faveItem;
				break;
			}
		}

		if (faveItemToRerank == null)
			return;

		faveList.getList().remove(faveItemToRerank);
		faveList.getList().add(newIndex, faveItemToRerank);
		ofy().save().entities(faveList).now();

		return;
	}

	@POST
	@Path(ApiPaths.EDIT_WHYLINE)
	@ApiOperation(value = "Edit a WhyLine")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "WhyLine did not pass validation"), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void editWhylineForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String hashtag, @QueryParam("songId") final String songID,
			@QueryParam("whyline") final String whyline) {

		Objects.requireNonNull(hashtag);
		Objects.requireNonNull(songID);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			// Whyline does not meet validation
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(whyline).build());

		final AppUser currentUser = AppUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), hashtag);
		Objects.requireNonNull(faveList);

		// Find the song to edit whyline
		FaveItem faveItemToEdit = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songID)) {
				faveItemToEdit = faveItem;
				break;
			}
		}

		Objects.requireNonNull(faveItemToEdit);

		// Set the denormalized whyline for the FaveItem
		faveItemToEdit.setWhyline(whyline);

		// Set the external Whyline
		final Ref<Whyline> currentWhyline = faveItemToEdit.getWhylineRef();
		if (currentWhyline == null) {
			// Create a new Whyline entity
			final Whyline whylineEntity = new Whyline(whyline, faveItemToEdit.getSongID(), currentUser.getUsername());
			ofy().save().entity(whylineEntity).now();
			faveItemToEdit.setWhylineRef(Ref.create(whylineEntity));
		}
		else {
			// Just modify the existing Whyline entity
			final Whyline whylineEntity = (Whyline)ofy().load().value(currentWhyline).get();
			whylineEntity.setWhyline(whyline);
			ofy().save().entity(whylineEntity).now();
		}

		ofy().save().entity(faveList).now();

		return;
	}
}
