package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Whyline extends DatastoreObject{

	public static final String SEPERATOR_TOKEN = ":";

	@Id private String id;
	private String whyline;
	@Index private Ref<Song> song;
	@Load private Ref<AppUser> user;
	// Need this because Ref<?> doesn't work on GWT side
	@IgnoreSave String username;
	private int score;

	@SuppressWarnings("unused")
	private Whyline() {}

	public Whyline(final String whyline, final String songID, final String username) {
		this.id = username+Whyline.SEPERATOR_TOKEN+songID;
		this.whyline = whyline;
		this.song = Ref.create(Key.create(Song.class, songID));
		this.user = Ref.create(Key.create(AppUser.class, username));
	}

	public static Whyline findWhyline(final String id) {
		return ofy().load().type(Whyline.class).id(id).get();
	}

	public static List<Whyline> getWhylinesForSong(final Song song) {
		final List<Whyline> whylines = ofy().load().type(Whyline.class).filter("song", Ref.create(song)).limit(15).list();
		for(final Whyline whyline : whylines) {
			whyline.setUsername(whyline.getUser().getUsername());
		}
		return whylines;
	}


	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
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

	public AppUser getUser() {
		return user.get();
	}

	public void setUser(final AppUser user) {
		this.user = Ref.create(user);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public int getScore() {
		return score;
	}

	public void setScore(final int score) {
		this.score = score;
	}
}
