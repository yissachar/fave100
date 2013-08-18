package com.fave100.server.domain.favelist;

import com.fave100.server.domain.Whyline;
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
public class FaveItem {

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

	public Ref<Whyline> getWhylineRef() {
		return whylineRef;
	}

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
