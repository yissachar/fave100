package com.fave100.server.domain.favelist;

import java.io.Serializable;

import com.fave100.server.domain.Whyline;
import com.fave100.shared.domain.FaveItemDto;
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
public class FaveItem extends FaveItemDto implements Serializable {

	// This field MUST be updated if this class is changed in a way that affects serialization: http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
	private static final long serialVersionUID = -8899544665404145248L;

	@Index private String songID;
	private Ref<Whyline> whylineRef;

	@SuppressWarnings("unused")
	private FaveItem() {
	}

	public FaveItem(final String song, final String artist, final String songID) {
		super(song, artist);
		this.setSongID(songID);
	}

	/* Getters and Setters */

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Whyline> getWhylineRef() {
		return whylineRef;
	}

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
