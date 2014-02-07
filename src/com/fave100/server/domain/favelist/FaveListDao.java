package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fave100.server.domain.Song;
import com.fave100.server.domain.UserListResult;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.ValidationException;
import com.fave100.shared.exceptions.favelist.BadWhylineException;
import com.fave100.shared.exceptions.favelist.FaveListAlreadyExistsException;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.favelist.TooManyFaveListsException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.inject.Inject;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;

public class FaveListDao {

	public static final String SEPERATOR_TOKEN = ":";
	public static final int MAX_FAVES = 100;

	private AppUserDao appUserDao;

	@Inject
	public FaveListDao(AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}

	public FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}

	public FaveList findFaveList(final String username, final String hashtag) {
		return findFaveList(username.toLowerCase() + FaveListDao.SEPERATOR_TOKEN + hashtag.toLowerCase());
	}

	public void addFaveListForCurrentUser(final String hashtagName) throws Exception {
		final String error = Validator.validateHashtag(hashtagName);
		if (error != null) {
			throw new ValidationException(error);
		}

		final AppUser currentUser = appUserDao.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		// -1 because #fave100 is a default list not stored in the hashtags list
		if (currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER - 1)
			throw new TooManyFaveListsException("You can't have more than " + Constants.MAX_LISTS_PER_USER + " lists");

		final String username = currentUser.getUsername();

		if (findFaveList(username, hashtagName) != null)
			throw new FaveListAlreadyExistsException("You already have a list with that name");

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
	}

	public void deleteFaveListForCurrentUser(final String listName) throws NotLoggedInException {
		final AppUser currentUser = appUserDao.getLoggedInAppUser();
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
	}

	public void addFaveItemForCurrentUser(final String hashtag, final String songID)
			throws Exception {

		final AppUser currentUser = appUserDao.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = findFaveList(currentUser.getUsername(), hashtag);
		if (faveList.getList().size() >= FaveListDao.MAX_FAVES)
			throw new SongLimitReachedException();

		// Get the song from Lucene lookup
		final Song song = Song.findSong(songID);
		if (song == null)
			return;

		final FaveItem newFaveItem = new FaveItem(song.getSong(), song.getArtist(), song.getId());

		// Check if it is a unique song for this user
		boolean unique = true;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(newFaveItem.getSongID())) {
				unique = false;
			}
		}

		if (!unique)
			throw new SongAlreadyInListException();

		// Create the new FaveItem
		faveList.getList().add(newFaveItem);
		ofy().save().entities(faveList).now();
	}

	public void removeFaveItemForCurrentUser(final String hashtag, final String songID) throws NotLoggedInException {
		final AppUser currentUser = appUserDao.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = findFaveList(currentUser.getUsername(), hashtag);
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
	}

	public void rerankFaveItemForCurrentUser(final String hashtag, final String songID, final int newIndex) throws NotLoggedInException {

		final AppUser currentUser = appUserDao.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Make sure new index is valid
		if (newIndex < 0 || newIndex >= faveList.getList().size())
			throw new IllegalArgumentException("Index out of range");

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
	}

	public void editWhylineForCurrentUser(final String hashtag, final String songID, final String whyline)
			throws NotLoggedInException, BadWhylineException {
		Objects.requireNonNull(hashtag);
		Objects.requireNonNull(songID);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			throw new BadWhylineException(whylineError);

		final AppUser currentUser = appUserDao.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = findFaveList(currentUser.getUsername(), hashtag);
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
	}

	public List<FaveItem> getFaveList(final String username, final String hashtag) {
		final FaveList faveList = findFaveList(username, hashtag);
		if (faveList == null)
			return null;
		return faveList.getList();
	}

	public List<FaveItem> getMasterFaveList(final String hashtag) {
		return ofy().load().type(Hashtag.class).id(hashtag.toLowerCase()).get().getList();
	}

	public List<String> getHashtagAutocomplete(final String searchTerm) {
		final List<String> names = new ArrayList<String>();
		if (searchTerm.isEmpty())
			return names;

		// TODO: Need to sort by popularity
		final List<Hashtag> hashtags = ofy().load().type(Hashtag.class).filter("id >=", searchTerm.toLowerCase()).filter("id <", searchTerm.toLowerCase() + "\uFFFD").limit(5).list();
		for (final Hashtag hashtag : hashtags) {
			names.add(hashtag.getName());
		}
		return names;
	}

	public Hashtag getHashtag(final String id) {
		return ofy().load().type(Hashtag.class).id(id).get();
	}

	public double calculateItemScore(final int position) {
		return ((double)(-1 * position) / 11 + ((double)111 / 11));
	}

	public List<String> getTrendingFaveLists() {
		// Nov 26 2013: Temporarily disabling proper trending in favor of hard-coded popular lists
		//		List<Hashtag> hashtags = ofy().load().type(Hashtag.class).order("-zscore").limit(5).list();
		//		List<String> trending = new ArrayList<>();
		//		for (Hashtag hashtag : hashtags) {
		//			trending.add(hashtag.getName());
		//		}
		//		return trending;
		List<String> trending = new ArrayList<>();
		trending.add("alltime");
		trending.add("2014");
		trending.add("2013");
		trending.add("2012");
		trending.add("dance");
		trending.add("classicrock");
		return trending;
	}

	public List<UserListResult> getListsContainingSong(final String songID) {
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

}
