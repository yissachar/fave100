package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.fave100.server.domain.ApiBase;
import com.fave100.server.domain.SongApi;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.UserListResult;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserApi;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.inject.Inject;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;

public class FaveListApi extends ApiBase {

	public static final String FAVELIST_PATH = "favelist";

	private FaveListDao _faveListDao;
	private AppUserApi _appUserApi;
	private SongApi _songApi;

	@Inject
	public FaveListApi(FaveListDao faveListDao, AppUserApi appUserApi, SongApi songApi) {
		_faveListDao = faveListDao;
		_appUserApi = appUserApi;
		_songApi = songApi;
	}

	@ApiMethod(name = "faveList.getFaveList", path = "getfavelist")
	public List<FaveItem> getFaveList(@Named("username") final String username, @Named("hashtag") final String hashtag) {
		final FaveList faveList = _faveListDao.findFaveList(username, hashtag);
		if (faveList == null)
			return null;
		return faveList.getList();
	}

	@ApiMethod(name = "faveList.getMasterFaveList", path = FAVELIST_PATH + "/masterFaveList")
	public List<FaveItem> getMasterFaveList(@Named("hashtag") final String hashtag) {
		return ofy().load().type(Hashtag.class).id(hashtag).get().getList();
	}

	@ApiMethod(name = "faveList.getHashtagAutocomplete", path = FAVELIST_PATH + "/hashtagAutocomplete")
	public List<StringResult> getHashtagAutocomplete(@Named("searchTerm") final String searchTerm) {
		final List<StringResult> names = new ArrayList<>();
		if (searchTerm.isEmpty())
			return names;

		// TODO: Need to sort by popularity
		final List<Hashtag> hashtags = ofy().load().type(Hashtag.class).filter("id >=", searchTerm.toLowerCase()).filter("id <", searchTerm.toLowerCase() + "\uFFFD").limit(5).list();
		for (final Hashtag hashtag : hashtags) {
			names.add(new StringResult(hashtag.getName()));
		}
		return names;
	}

	@ApiMethod(name = "faveList.getTrendingFaveLists", path = FAVELIST_PATH + "/trendingFaveLists")
	public List<StringResult> getTrendingFaveLists() {
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
		return trending;
	}

	@ApiMethod(name = "faveList.getListsContainingSong", path = FAVELIST_PATH + "/listsContainingSong")
	public List<UserListResult> getListsContainingSong(@Named("songID") final String songID) {
		final List<UserListResult> userListResults = new ArrayList<>();

		// Get up to 30 FaveLists containing the song
		final List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("list.songID", songID).limit(30).list();

		// Get the user's avatars
		for (final FaveList faveList : faveLists) {
			ofy().load().ref(faveList.getUser());
			final AppUser user = faveList.getUser().get();
			String avatar = "";
			if (user != null)
				avatar = user.getAvatarImage(30);

			UserListResult userListResult = new UserListResult(user.getUsername(), faveList.getHashtag(), avatar);
			userListResults.add(userListResult);
		}
		return userListResults;
	}

	@ApiMethod(name = "faveList.add", path = FAVELIST_PATH + "/add")
	public void addFaveListForCurrentUser(HttpServletRequest request, @Named("hashtag") final String hashtagName) throws BadRequestException, UnauthorizedException, ForbiddenException {
		final String error = Validator.validateHashtag(hashtagName);
		if (error != null) {
			throw new BadRequestException(error);
		}

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		// -1 because #fave100 is a default list not stored in the hashtags list
		if (currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER - 1)
			// TooManyFaveLists
			throw new ForbiddenException("You can't have more than " + Constants.MAX_LISTS_PER_USER + " lists");

		final String username = currentUser.getUsername();

		if (_faveListDao.findFaveList(username, hashtagName) != null)
			// FaveListAlready exists
			throw new ForbiddenException("You already have a list with that name");

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

	@ApiMethod(name = "faveList.delete", path = FAVELIST_PATH + "/delete")
	public void deleteFaveListForCurrentUser(HttpServletRequest request, @Named("list") final String listName) throws UnauthorizedException {
		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

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

	@ApiMethod(name = "faveList.addFaveItem", path = FAVELIST_PATH + "item/add")
	public void addFaveItemForCurrentUser(HttpServletRequest request, @Named("list") final String hashtag, @Named("songId") final String songID)
			throws UnauthorizedException, ForbiddenException {

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);

		// Check FaveList size limit reached
		if (faveList.getList().size() >= FaveListDao.MAX_FAVES)
			throw new ForbiddenException("You have reached the maximum FaveList size");

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
			throw new ForbiddenException("The item already exists in this list");

		// Create the new FaveItem
		faveList.getList().add(newFaveItem);
		ofy().save().entities(faveList).now();

		return;
	}

	@ApiMethod(name = "faveList.removeFaveItem", path = FAVELIST_PATH + "/item/remove")
	public void removeFaveItemForCurrentUser(HttpServletRequest request, @Named("list") final String hashtag, @Named("songId") final String songID) throws NotLoggedInException {
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

	@ApiMethod(name = "faveList.rerankFaveItem", path = FAVELIST_PATH + "/item/rerank")
	public void rerankFaveItemForCurrentUser(HttpServletRequest request, @Named("list") final String hashtag, @Named("songId") final String songID,
			@Named("newIndex") final int newIndex) throws UnauthorizedException, BadRequestException {

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		final FaveList faveList = _faveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Make sure new index is valid
		if (newIndex < 0 || newIndex >= faveList.getList().size())
			throw new BadRequestException("Index out of range");

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

	@ApiMethod(name = "faveList.editWhyline", path = FAVELIST_PATH + "/item/whyline")
	public void editWhylineForCurrentUser(HttpServletRequest request, @Named("list") final String hashtag, @Named("songId") final String songID, @Named("whyline") final String whyline)
			throws BadRequestException, UnauthorizedException {
		Objects.requireNonNull(hashtag);
		Objects.requireNonNull(songID);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			// Whyline does not meet validation
			throw new BadRequestException(whylineError);

		final AppUser currentUser = _appUserApi.getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");
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
