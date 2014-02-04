package com.fave100.server.domain.favelist;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fave100.server.domain.Whyline;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Index;

/**
 * A song that a Fave100 user has added to their Fave100.
 * 
 * @author yissachar.radcliffe
 * 
 */
@Embed
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaveItem implements Serializable {

	// This field MUST be updated if this class is changed in a way that affects serialization: http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
	private static final long serialVersionUID = -8899544665404145248L;

	private String song;
	private String artist;
	@Index private String songID;
	private String whyline = "";
	private Ref<Whyline> whylineRef;

	@SuppressWarnings("unused")
	private FaveItem() {
	}

	public FaveItem(final String song, final String artist, final String songID) {
		this.setSong(song);
		this.setArtist(artist);
		this.setSongID(songID);
	}

	/* Getters and Setters */

	public String getSong() {
		return song;
	}

	public void setSong(final String song) {
		this.song = song;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(final String artist) {
		this.artist = artist;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Whyline> getWhylineRef() {
		return whylineRef;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setWhylineRef(final Ref<Whyline> whylineRef) {
		this.whylineRef = whylineRef;
	}

	public String getSongID() {
		return songID;
	}

	public void setSongID(final String songID) {
		this.songID = songID;
	}

	public String getId() {
		return songID;
	}

}