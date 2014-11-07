package com.fave100.server.domain.favelist;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fave100.server.domain.Whyline;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Index;
import com.wordnik.swagger.annotations.ApiModel;

/**
 * A song that a Fave100 user has added to their Fave100.
 * 
 * @author yissachar.radcliffe
 * 
 */
@Embed
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "FaveItem")
public class FaveItem implements Serializable {

	// This field MUST be updated if this class is changed in a way that affects serialization: http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
	private static final long serialVersionUID = -8899544665404145248L;

	private String song;
	private String artist;
	@Index private String songID;
	private String whyline = "";
	private Ref<Whyline> whylineRef;
	private Date datePicked;

	@SuppressWarnings("unused")
	private FaveItem() {
	}

	public FaveItem(final String song, final String artist, final String songID) {
		this.setSong(song);
		this.setArtist(artist);
		this.setSongID(songID);
		datePicked = new Date();
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
	public Ref<Whyline> getWhylineRef() {
		return whylineRef;
	}

	@JsonIgnore
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

	@JsonIgnore
	public Date getDatePicked() {
		return datePicked;
	}

	@JsonIgnore
	public void setDatePicked(Date datePicked) {
		this.datePicked = datePicked;
	}

}