package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.domain.Activity;
import com.fave100.server.domain.Activity.Transaction;
import com.fave100.server.domain.DatastoreObject;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

@Entity
public class FaveList extends DatastoreObject{

	@IgnoreSave public static final String SEPERATOR_TOKEN = ":";
	@IgnoreSave public static final String DEFAULT_HASHTAG = "fave100";
	@IgnoreSave public static final int MAX_FAVES = 100;

	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	private List<FaveItem> list = new ArrayList<FaveItem>();;

	@SuppressWarnings("unused")
	private FaveList() {}

	public FaveList(final String username, final String hashtag) {
		this.id = username+FaveList.SEPERATOR_TOKEN+hashtag;
		this.user = Ref.create(Key.create(AppUser.class, username));
		this.hashtag = hashtag;
	}

	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}

	// TODO: Do FaveList activities need to be transactional? If so, need to set AppUser as parent
	public static void addFaveItemForCurrentUser(final String hashtag, final String songTitle,
			final String artist)
					throws NotLoggedInException, SongLimitReachedException, SongAlreadyInListException {

		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) {
			throw new NotLoggedInException();
		}
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList.getList().size() >= FaveList.MAX_FAVES) throw new SongLimitReachedException();

		// Get the song from datastore or create it
		final Song song = Song.findSongByTitleAndArtist(songTitle, artist);
		if(song == null) return;

		final String songArtistID = song.getTitle()+Song.TOKEN_SEPARATOR+song.getArtist();
		final FaveItem newFaveItem = new FaveItem(song.getTitle(), song.getArtist(), songArtistID);

		// Check if it is a unique song for this user
		boolean unique = true;
		for(final FaveItem faveItem : faveList.getList()) {
			/*if(faveItem.getSong().equals(Ref.create(Key.create(Song.class, song.getId())))){
				unique = false;
			}*/
			if(faveItem.getSong().equals(newFaveItem.getSong())
					&& faveItem.getArtist().equals(newFaveItem.getArtist())) {
				unique = false;
			}
		}

		if(unique == false) throw new SongAlreadyInListException();;
		// Create the new FaveItem
		final Ref<Song> songRef = Ref.create(Key.create(Song.class, songArtistID));
		faveList.getList().add(newFaveItem);
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_ADDED);
		activity.setSong(songRef);
		ofy().save().entities(activity, faveList).now();
	}

	public static void removeFaveItemForCurrentUser(final String hashtag, final int index) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return;
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_REMOVED);
		//activity.setSong(faveList.getList().get(index).getSong());
		// We must also delete the whyline if it exists
	//	final Ref<Whyline> currentWhyline = faveList.getList().get(index).getWhyline();
	//	if(currentWhyline != null) {
	//		ofy().delete().entity(currentWhyline).now();
	//	}
		faveList.getList().remove(index);
		ofy().save().entities(activity, faveList).now();
	}

	public static void rerankFaveItemForCurrentUser(final String hashtag, final int currentIndex, final int newIndex) {
		// TODO: Use a transaction to ensure that the indices are correct
		// For some reason this throws a illegal state exception about deregistering a transaction that is not registered
//		ofy().transact(new VoidWork() {
//			public void vrun() {
				final AppUser currentUser = AppUser.getLoggedInAppUser();
				if(currentUser == null) return;
				final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
				if(faveList == null) return;
				final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_POSITION_CHANGED);
		//		activity.setSong(faveList.getList().get(currentIndex).getSong());
				activity.setPreviousLocation(currentIndex+1);
				activity.setNewLocation(newIndex+1);
				final FaveItem faveAtCurrIndex = faveList.getList().remove(currentIndex);
				faveList.getList().add(newIndex, faveAtCurrIndex);
				ofy().save().entities(activity, faveList).now();
//			}
//		});
	}

	public static void editWhylineForCurrentUser(final String hashtag, final int index, final String whyline) {
		//TODO: Sanitize the string
		//TODO: Length restriction?
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return;
		final FaveItem faveItem = faveList.getList().get(index);
		faveItem.setWhyline(whyline);
		ofy().save().entities(faveList, faveItem).now();
		/*final Ref<Whyline> currentWhyline = faveItem.getWhyline();
		if(currentWhyline == null) {
			// Need to create an external whyline
			final Song song = faveItem.getSong().get();
			final Whyline whylineEntity = new Whyline(whyline, song.getId(), currentUser.getUsername());
			final String whylineID = currentUser.getUsername()+Whyline.SEPERATOR_TOKEN+song.getId();
			faveItem.setWhyline(Ref.create(Key.create(Whyline.class, whylineID)));
			ofy().save().entities(faveList, whylineEntity).now();
		} else {
			// Just modify the existing whyline
			currentWhyline.get().setWhyline(whyline);
			ofy().save().entity(currentWhyline).now();
		}*/
	}

	public static List<FaveItem> getFaveItemsForCurrentUser(final String hashtag) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		return getFaveList(currentUser.getUsername(), hashtag);

	}

	public static List<FaveItem> getFaveList(final String username, final String hashtag) {
		final FaveList faveList = ofy().load().type(FaveList.class).id(username+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return null;
		return faveList.getList();
		/*final ArrayList<Song> songArray = new ArrayList<Song>();
		for(final FaveItem faveItem : faveList.getList()) {
			final Song song = new Song(faveItem.getSong(), faveItem.getArtist(), "fake");//faveItem.getSong().get();
			if(song != null) {
				//String whyline = "";
				//final Ref<Whyline> whylineRef = faveItem.getWhyline();
				//if(whylineRef != null) {
				//	whyline = whylineRef.get().getWhyline();
				//}
				//song.setWhyline(whyline);
				song.setWhyline(faveItem.getWhyline());
				songArray.add(song);
			} else {
				Logger.getAnonymousLogger().log(Level.SEVERE, "Null song entry for favelist "+faveList.id);
			}
		}
		return songArray;*/
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
