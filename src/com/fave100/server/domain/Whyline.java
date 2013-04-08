package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.PojoIf;

@Entity
public class Whyline extends DatastoreObject {

	// Lets us know whether or not to index song
	static class WhylineCheck extends PojoIf<Whyline> {
		@Override
		public boolean matchesPojo(final Whyline pojo) {
			return pojo.whyline != null && !pojo.whyline.isEmpty();
		}
	}

	@Id private Long id;
	private String whyline;
	private String username;
	@Index(WhylineCheck.class) private Ref<Song> song;

	public Whyline() {}

	public Whyline(final String whyline, final String songID, final String username) {
		this.whyline = whyline;
		this.song = Ref.create(Key.create(Song.class, songID));
		this.username = username;
	}

	public static Whyline findWhyline(final Long id) {
		return ofy().load().type(Whyline.class).id(id).get();
	}

	public static List<Whyline> getWhylinesForSong(final Song song) {
		final List<Whyline> whylines = ofy().load().type(Whyline.class).filter("song", Ref.create(song)).limit(15).list();
		return whylines;
	}

	/* Getters and Setters */

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

}
