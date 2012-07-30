package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fave100.server.DAO;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;

/**
 * A song that a Fave100 user has added to their Fave100.
 * @author yissachar.radcliffe
 *
 */
@Entity
public class FaveItem extends DatastoreObject{
		
	private Key<AppUser> appUser;
	private Key<Song> song;
	private String whyline;
	/* We will only use the following fields as a convenient way to send
	 * a single object back to the client containing Song data and Fave data,
	 * but will not actually store the data in the datastore. 
	 */
	@NotSaved private String trackName;
	@NotSaved private String trackViewUrl;
	@NotSaved private String artistName;
	@NotSaved private String releaseYear;
	
	public static final Objectify ofy() {
		DAO dao = new DAO();
		return dao.ofy();
	}
	// TODO: id not safe? can have same id's if different parents? use keys instead or confirm that id's are safe
	public static FaveItem findFaveItem(Long id) {
		return ofy().get(FaveItem.class, id);
	}
	
	public static void removeFaveItemForCurrentUser(Long id) {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		FaveItem faveItemToDelete = ofy().get(FaveItem.class, id);
		// Verify that user deleting this FaveItem is the user that the FaveItem belongs to		
		if(faveItemToDelete.getAppUser().equals(new Key<AppUser>(AppUser.class, currentUser.getUsername()))) {
			ofy().delete(FaveItem.class, id);
		}
	}
	
	public static List<FaveItem> getAllFaveItemsForCurrentUser() {
		// TODO: Check if this is most efficient way to do this
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		Key<AppUser> currentUserKey = new Key<AppUser>(AppUser.class, currentUser.getUsername());
		// Get the actual FaveItems list - we will send this to the client after adding the song data
		List<FaveItem> allFaveItemsForUser = ofy().query(FaveItem.class).filter("appUser", currentUserKey).list();
		// Get the song keys from the FaveItem list
		List<Key<Song>> songKeys = new ArrayList<Key<Song>>();
		for(FaveItem faveItem : allFaveItemsForUser) {
			songKeys.add(faveItem.song);
		}
		// Now that we have song keys, get actual songs in batch get
		Map<Key<Song>, Song> songsForFaveItems = ofy().get(songKeys);
		// Add the song data to the FaveItems that we will send back to the client
		for(FaveItem faveItem : allFaveItemsForUser) {
			Song song = songsForFaveItems.get(faveItem.song);
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());
		}
		return allFaveItemsForUser;
	}
	
	public static void addFaveItemForCurrentUser(Long songID, Song songProxy) {
		// TODO: Verify integrity of songProxy on server-side?
		// TODO: Only allow user to store 100 faveItems
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		Song song = ofy().find(Song.class, songID);
		// If the song does not exist, create it
		if(song == null) {
			songProxy.setId(songID);
			songProxy.persist();
		}		
		// Create the new FaveItem 
		FaveItem newFaveItem = new FaveItem();
		newFaveItem.setAppUser(new Key<AppUser>(AppUser.class, currentUser.getUsername()));
		newFaveItem.setSong(new Key<Song>(Song.class, songID));
		newFaveItem.persist();
	}
	
	public FaveItem persist() {
		ofy().put(this);
		return this;
	}
	
	public void remove() {
		ofy().delete(this);
	}
	
	/*Getters and Setters */	

	public Key<AppUser> getAppUser() {
		return appUser;
	}

	public void setAppUser(Key<AppUser> appUser) {
		this.appUser = appUser;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(String whyline) {
		this.whyline = whyline;
	}

	public Key<Song> getSong() {
		return song;
	}

	public void setSong(Key<Song> song) {
		this.song = song;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}	
	
}
