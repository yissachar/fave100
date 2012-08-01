package com.fave100.server.domain;

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
public class FaveItem extends DatastoreObject{//TODO: No need to extend from datastore?
		
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
	
	public FaveItem persist() {
		ofy().put(this);
		return this;
	}
	
	public void remove() {
		ofy().delete(this);
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
