package com.fave100.server.domain.favelist;

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
import com.fave100.server.domain.SongApi;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.StringResultCollection;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserApi;
import com.fave100.server.exceptions.FaveItemAlreadyInListException;
import com.fave100.server.exceptions.FaveListAlreadyExistsException;
import com.fave100.server.exceptions.FaveListLimitReachedException;
import com.fave100.server.exceptions.FaveListSizeReachedException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.inject.Inject;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.FAVELIST_ROOT)
@Api(value = "/" + ApiPaths.FAVELIST_ROOT, description = "Operations on FaveLists")
public class FaveListApi {

	private FaveListDao _faveListDao;
	private AppUserApi _appUserApi;
	private SongApi _songApi;

	@Inject
	public FaveListApi(FaveListDao faveListDao, AppUserApi appUserApi, SongApi songApi) {
		_faveListDao = faveListDao;
		_appUserApi = appUserApi;
		_songApi = songApi;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{username}/{hashtag}")
	@ApiOperation(value = "Get a user's FaveList", response = FaveItemCollection.class)
	public FaveItemCollection getFaveList(
			@PathParam("username") final String username,
			@PathParam("hashtag") final String hashtag) {

		final FaveList faveList = _faveListDao.findFaveList(username, hashtag);
		if (faveList == null)
			return null;

		return new FaveItemCollection(faveList.getList());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{hashtag}")
	@ApiOperation(value = "Get a master FaveList", response = FaveItemCollection.class)
	public FaveItemCollection getMasterFaveList(@PathParam("hashtag") final String hashtag) {
		return new FaveItemCollection(ofy().load().type(Hashtag.class).id(hashtag.toLowerCase()).get().getList());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.GET_HASHTAG_AUTOCOMPLETE)
	@ApiOperation(value = "Get list name suggestions", response = StringResultCollection.class)
	public StringResultCollection getHashtagAutocomplete(@QueryParam("searchTerm") final String searchTerm) {

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
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.TRENDING_FAVELISTS)
	@ApiOperation(value = "Get a list of trending FaveLists", response = StringResultCollection.class)
	public StringResultCollection getTrendingFaveLists() {
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.ADD_FAVELIST)
	@ApiOperation(value = "Add a FaveList")
	public void addFaveListForCurrentUser(@Context HttpServletRequest request, @QueryParam("hashtag") final String hashtagName) {

		final String error = Validator.validateHashtag(hashtagName);
		if (error != null) {
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
		}

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		// -1 because #alltime is a default list not stored in the hashtags list
		if (currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER - 1)
			throw new FaveListLimitReachedException();

		final String username = currentUser.getUsername();

		if (_faveListDao.findFaveList(username, hashtagName) != null)
			throw new FaveListAlreadyExistsException();

		currentUser.getHashtags().add(hashtagName);
		final FaveList faveList = new FaveList(username, hashtagName);
		// Transaction to ensure no duplicate hashtags created
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				Hashtag hashtag = ofy().load().type(Hashtag.class).id(hashtagName).get();
				// Hashtag already exists, add it to user's lists
				if (hashtag != null) {
					ofy().save().entities(currentUser, faveList).now();
				}
				// Create a new hashtag
				else {
					hashtag = new Hashtag(hashtagName, username);
					ofy().save().entities(currentUser, faveList, hashtag).now();
				}
			}
		});

		return;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.DELETE_FAVELIST)
	public void deleteFaveListForCurrentUser(HttpServletRequest request, @QueryParam("list") final String listName) {
		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.ADD_FAVEITEM)
	public void addFaveItemForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String hashtag, @QueryParam("songId") final String songID) {

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);

		// Check FaveList size limit reached
		if (faveList.getList().size() >= FaveListDao.MAX_FAVES)
			throw new FaveListSizeReachedException();

		// Get the song from Lucene lookup
		final FaveItem newFaveItem = _songApi.getSong(songID);
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
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.REMOVE_FAVEITEM)
	public void removeFaveItemForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String hashtag, @QueryParam("songId") final String songID) {

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);
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
	@Path("rerank/{list}/{songId}/{newIndex}")
	public void rerankFaveItemForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String hashtag, @PathParam("songId") final String songID,
			@PathParam("newIndex") final int newIndex) {

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Make sure new index is valid
		if (newIndex < 0 || newIndex >= faveList.getList().size())
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Index out of range").build());

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
	@Produces(MediaType.APPLICATION_JSON)
	@Path(ApiPaths.EDIT_WHYLINE)
	public void editWhylineForCurrentUser(@Context HttpServletRequest request, @QueryParam("list") final String hashtag, @QueryParam("songId") final String songID,
			@QueryParam("whyline") final String whyline) {

		Objects.requireNonNull(hashtag);
		Objects.requireNonNull(songID);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			// Whyline does not meet validation
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(whyline).build());

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);
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
