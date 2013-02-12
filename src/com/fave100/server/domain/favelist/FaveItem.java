package com.fave100.server.domain.favelist;

import com.fave100.server.domain.Song;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Index;

/**
 * A song that a Fave100 user has added to their Fave100.
 * @author yissachar.radcliffe
 *
 */
@Embed
public class FaveItem {

	//@Id private Long id;
//	@Load private Ref<Song> song;
//	@Load private Ref<Whyline> whyline;
	private String song;
	private String artist;
	private String whyline;
	// TODO: Only index if whyline not null
	@Index private Ref<Song> songRef;

	@SuppressWarnings("unused")
	private FaveItem() {}

	public FaveItem(final String song, final String artist, final String songID) {
		this.setSong(song);
		this.setArtist(artist);
		this.setSongRef(Ref.create(Key.create(Song.class, songID)));
	}

	/*public FaveItem(final String song, final String artist, final String whyline) {
		//song = Ref.create(Key.create(Song.class, songID));
		this.setSong(song);
		this.setArtist(artist);
		this.setWhyline(whyline);
		/*if(whylineID != null) {
			whyline =  Ref.create(Key.create(Whyline.class, whylineID));
		}*/
	//}

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

	public Ref<Song> getSongRef() {
		return songRef;
	}

	public void setSongRef(final Ref<Song> songRef) {
		this.songRef = songRef;
	}


	// Getters and setters

/*	public Ref<Song> getSong() {
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
*/
	/*public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}*/

}
