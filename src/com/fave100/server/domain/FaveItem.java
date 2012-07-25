package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.DAO;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;

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
	
	public static FaveItem findFaveItem(Long id) {
		return ofy().get(FaveItem.class, id);
	}
	
	public static void removeFaveItem(Long id) {
		ofy().delete(FaveItem.class, id);
	}
	
	public static List<FaveItem> getAllFaveItemsForCurrentUser() {
		// TODO: Check if this is most efficient way to do this
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		Key<AppUser> currentUserKey = new Key<AppUser>(AppUser.class, currentUser.getId());
		List<FaveItem> allFaveItemsForUser = ofy().query(FaveItem.class).filter("appUser", currentUserKey).list();
		for(FaveItem faveItem : allFaveItemsForUser) {
			Song song = ofy().get(faveItem.song);
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());
		}
		return allFaveItemsForUser;
	}
	
	public static void addFaveItemForCurrentUser(Long songID, Song songProxy) {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		Song song = ofy().find(Song.class, songID);
		// If the song does not exist, create it
		if(song == null) {
			/*song = new Song();
			song.setId(songID);
			song.setTitle(songProxy.getTitle());
			song.setArtist(songProxy.getArtist());
			song.setItemURL(songProxy.getItemURL());
			song.setReleaseYear(songProxy.getReleaseYear());
			song.persist();*/
			songProxy.setId(songID);
			//songProxy.setArtistId(songProxy.getArtistId());
			songProxy.persist();
		}		
		// Create the new FaveItem 
		FaveItem newFaveItem = new FaveItem();
		newFaveItem.setAppUser(new Key<AppUser>(AppUser.class, currentUser.getId()));
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
