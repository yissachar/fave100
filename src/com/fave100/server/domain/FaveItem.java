package com.fave100.server.domain;

import com.googlecode.objectify.Key;
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
	@Load private Ref<Whyline> whyline;
	
	@SuppressWarnings("unused")
	private FaveItem() {}
	
	public FaveItem(final Long songID) {
		this(songID, null);
	}
	
	public FaveItem(final Long songID, final Long whylineID) {
		song = Ref.create(Key.create(Song.class, songID));
		if(whylineID != null) {
			whyline =  Ref.create(Key.create(Whyline.class, whylineID));
		}
	}
	
	
	// Getters and setters
	
	public Ref<Song> getSong() {
		return song;
	}

	public void setSong(final Ref<Song> song) {
		this.song = song;
	}

	public Ref<Whyline> getWhyline() {
		return whyline;
	}

	public void setWhyline(final Ref<Whyline> whyline) {
		this.whyline = whyline;
	}

	/*public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}*/
	
}
