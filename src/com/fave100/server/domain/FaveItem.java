package com.fave100.server.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Load;

/**
 * A song that a Fave100 user has added to their Fave100.
 * @author yissachar.radcliffe
 *
 */
@Embed
public class FaveItem {
		
	//@Id private Long id;
	@Load private Ref<Song> song;
	private String whyline;
	
	
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

	/*public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}*/
	
}
