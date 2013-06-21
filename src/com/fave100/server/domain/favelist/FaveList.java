package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fave100.server.domain.DatastoreObject;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.favelist.BadWhylineException;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

@Entity
public class FaveList extends DatastoreObject {

	@IgnoreSave public static final String SEPERATOR_TOKEN = ":";
	@IgnoreSave public static final int MAX_FAVES = 100;

	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	private List<FaveItem> list = new ArrayList<FaveItem>();

	@SuppressWarnings("unused")
	private FaveList() {
	}

	public FaveList(final String username, final String hashtag) {
		this.id = username.toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag;
		this.user = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		this.hashtag = hashtag;
	}

	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}

	public static void addFaveItemForCurrentUser(final String hashtag, final String songID)
			throws Exception {

		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if (currentUser == null) {
			throw new NotLoggedInException();
		}

		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername().toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag).get();
		if (faveList.getList().size() >= FaveList.MAX_FAVES)
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

		if (unique == false)
			throw new SongAlreadyInListException();
		;
		// Create the new FaveItem
		faveList.getList().add(newFaveItem);
		ofy().save().entities(faveList).now();
	}

	public static void removeFaveItemForCurrentUser(final String hashtag, final String songID) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if (currentUser == null)
			return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername().toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag).get();
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

	public static void rerankFaveItemForCurrentUser(final String hashtag, final String songID, final int newIndex) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if (currentUser == null)
			return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername().toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag).get();
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

	public static void editWhylineForCurrentUser(final String hashtag, final String songID, final String whyline)
			throws NotLoggedInException, BadWhylineException {
		Objects.requireNonNull(hashtag);
		Objects.requireNonNull(songID);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			throw new BadWhylineException(whylineError);

		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = ofy().load().type(FaveList.class)
				.id(currentUser.getUsername().toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag).get();
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

	// TODO: June 20, 2013 Check why this is a separate method
	public static List<FaveItem> getFaveListForCurrentUser(final String hashtag) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if (currentUser == null)
			return null;
		return getFaveList(currentUser.getUsername(), hashtag);

	}

	public static List<FaveItem> getFaveList(final String username, final String hashtag) {
		final FaveList faveList = ofy().load().type(FaveList.class).id(username.toLowerCase() + FaveList.SEPERATOR_TOKEN + hashtag).get();
		if (faveList == null)
			return null;
		return faveList.getList();
	}

	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(final String hashtag) {
		this.hashtag = hashtag;
	}

	public List<FaveItem> getList() {
		return list;
	}

}
