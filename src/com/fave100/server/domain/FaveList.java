package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.domain.Activity.Transaction;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

@Entity @Index
public class FaveList extends DatastoreObject{
	
	@IgnoreSave public static final String SEPERATOR_TOKEN = ":";
	@IgnoreSave public static final String DEFAULT_HASHTAG = "fave100";
	@IgnoreSave public static final int MAX_FAVES = 100;
	
	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	private List<FaveItem> list = new ArrayList<FaveItem>();;
	
	@SuppressWarnings("unused")
	// Request Factory calls this in some way, so need to make sure
	// that we initialize the list
	private FaveList() {		
		//list = new ArrayList<FaveItem>();
	}
	
	public FaveList(final String username, final String hashtag) {
		this.id = username+FaveList.SEPERATOR_TOKEN+hashtag;
		this.user = Ref.create(Key.create(AppUser.class, username));
		this.hashtag = hashtag;		
	}
	
	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}
	
	public static void addFaveItemForCurrentUser(final String hashtag, final Long songID, final Song songProxy) {
		// TODO: Verify integrity of songProxy on server-side? 
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) {
			throw new RuntimeException("Please log in to complete this action");
			//return false;
		}
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();		
		if(faveList.getList().size() >= FaveList.MAX_FAVES) throw new RuntimeException("You cannot have more than 100 songs in list");;		
		final Song song = ofy().load().type(Song.class).id(songID).get();		
		boolean unique = true;
		// If the song does not exist, create it
		if(song == null) {
			songProxy.setId(songID);
			ofy().save().entity(songProxy);
		} else {
			// Check if it is a unique song for this user
			for(final FaveItem faveItem : faveList.getList()) {
				if(faveItem.getSong().get().getId().equals(song.getId())) unique = false;
			}
		}
		if(unique == false) throw new RuntimeException("The song is already in your list");;
		// Create the new FaveItem 
		final FaveItem newFaveItem = new FaveItem();
		final Ref<Song> songRef = Ref.create(Key.create(Song.class, songID));
		newFaveItem.setSong(songRef);
		faveList.getList().add(newFaveItem);
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_ADDED);
		activity.setSong(songRef);
		ofy().save().entities(currentUser, activity, faveList).now();
	}
	
	public static void removeFaveItemForCurrentUser(final String hashtag, final int index) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;	
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return;
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_REMOVED);
		activity.setSong(faveList.getList().get(index).getSong());
		faveList.getList().remove(index);
		ofy().save().entities(currentUser, activity, faveList).now();	
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
				activity.setSong(faveList.getList().get(currentIndex).getSong());
				activity.setPreviousLocation(currentIndex+1);
				activity.setNewLocation(newIndex+1);
				final FaveItem faveAtCurrIndex = faveList.getList().remove(currentIndex);
				faveList.getList().add(newIndex, faveAtCurrIndex);				
				ofy().save().entities(currentUser, activity, faveList).now();
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
		faveList.getList().get(index).setWhyline(whyline);
		ofy().save().entities(currentUser, faveList).now();
	}	
	
	
	public static List<Song> getMasterFaveList() {
		// TODO: For now, run on ever page refresh but should really be a background task
		// TODO: Performance critical - optimize! This code is horrible performance-wise!
		final List<Song> allSongs = ofy().load().type(Song.class).list();
		for(final Song song : allSongs) {
			song.setScore(0);
			ofy().save().entity(song).now();
		}		
		final List<AppUser> allAppUsers = ofy().load().type(AppUser.class).list();
		for(final AppUser appUser : allAppUsers) {
			final FaveList faveList = ofy().load().type(FaveList.class).id(appUser.getUsername()+FaveList.SEPERATOR_TOKEN+FaveList.DEFAULT_HASHTAG).get();
			if(faveList != null) {
				for(int i = 0; i < faveList.getList().size(); i++) {
					final Song song = faveList.getList().get(i).getSong().get();
					song.addScore(FaveList.MAX_FAVES - i);
					ofy().save().entity(song).now();
				}
			}				
		}		
		final List<Song> topSongs = ofy().load().type(Song.class).order("-score").limit(100).list();		
		return topSongs;
	}
	
	public static List<Song> getFaveItemsForCurrentUser(final String hashtag) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		return getFaveList(currentUser.getUsername(), hashtag);
		
	}
	
	public static List<Song> getFaveList(final String username, final String hashtag) {
		final FaveList faveList = ofy().load().type(FaveList.class).id(username+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return null;
		final ArrayList<Song> songArray = new ArrayList<Song>();
		for(final FaveItem faveItem : faveList.getList()) {
			final Song song = faveItem.getSong().get();
			song.setWhyline(faveItem.getWhyline());
			songArray.add(song);
			/*final Song song = faveItem.getSong().get();
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());
			faveItem.setArtworkUrl60(song.getArtworkUrl60());*/
		}
		//return faveList.getList();
		return songArray;
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
