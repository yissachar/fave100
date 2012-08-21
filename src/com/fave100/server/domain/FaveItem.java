package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * A song that a Fave100 user has added to their Fave100.
 * @author yissachar.radcliffe
 *
 */
@Entity
@Index
public class FaveItem extends DatastoreObject{
		
	@Id private Long id;
	@Load private Ref<Song> song;
	private String whyline;
	/* We will only use the following fields as a convenient way to send
	 * a single object back to the client containing Song data and Fave data,
	 * but will not actually store the data in the datastore. 
	 */
	@IgnoreSave private String trackName;
	@IgnoreSave private String trackViewUrl;
	@IgnoreSave private String artistName;
	@IgnoreSave private String releaseYear;
	@IgnoreSave private String artworkUrl60;
		
	public static FaveItem findFaveItem(final Long id) {
		return ofy().load().type(FaveItem.class).id(id).get();		
	}
	
	// Getters and setters

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	public Ref<Song> getSong() {
		return song;
	}

	public void setSong(final Ref<Song> song) {
		this.song = song;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(final String trackName) {
		this.trackName = trackName;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(final String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public String getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(final String releaseYear) {
		this.releaseYear = releaseYear;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getArtworkUrl60() {
		return artworkUrl60;
	}

	public void setArtworkUrl60(final String artworkUrl60) {
		this.artworkUrl60 = artworkUrl60;
	}	
	
}
