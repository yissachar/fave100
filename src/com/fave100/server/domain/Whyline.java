package com.fave100.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity @Index // TODO: unindex
public class Whyline {
	
	public static final String SEPERATOR_TOKEN = ":";
	
	@Id private String id;
	private String whyline;
	private Ref<Song> song;
	private Ref<AppUser> user;
	private int score;
	
	@SuppressWarnings("unused")
	private Whyline() {}
	
	public Whyline(final String whyline, final Long songID, final String username) {
		this.id = username+Whyline.SEPERATOR_TOKEN+songID;
		this.whyline = whyline;
		this.song = Ref.create(Key.create(Song.class, songID));
		this.user = Ref.create(Key.create(AppUser.class, username));
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

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

	public int getScore() {
		return score;
	}

	public void setScore(final int score) {
		this.score = score;
	}
}
