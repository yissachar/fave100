package com.fave100.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.PojoIf;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Whyline {

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
	private String list;
	@Ignore String avatar;

	public Whyline() {
	}

	public Whyline(final String whyline, final String songID, final String username, final String list) {
		this.whyline = whyline;
		this.song = Ref.create(Key.create(Song.class, songID));
		this.username = username;
		this.list = list;
	}

	/* Getters and Setters */

	@JsonIgnore
	public Long getId() {
		return id;
	}

	@JsonIgnore
	public void setId(final Long id) {
		this.id = id;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	@JsonIgnore
	public Ref<Song> getSong() {
		return song;
	}

	@JsonIgnore
	public void setSong(final Ref<Song> song) {
		this.song = song;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

}
