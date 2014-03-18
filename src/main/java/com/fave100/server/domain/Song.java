package com.fave100.server.domain;

import com.fave100.shared.FaveItemInterface;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

/**
 * Represents a Song that users can add to their lists. This Song will not
 * actually be persisted directly in the datastore. Instead we will lookup a
 * Song from Lucene API and then store a denormalized embedded FaveItem
 * representing the Song.
 * 
 * @author yissachar.radcliffe
 * 
 */
@Entity
public class Song implements FaveItemInterface {

	@IgnoreSave public static String YOUTUBE_API_KEY = "";

	@Id private String id;
	private String artist;
	private String song;

	public Song() {
	}

	public Song(final String name, final String artist, final String id) {
		this.song = name;
		this.artist = artist;
		this.id = id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;
		if (((Song)obj).getId().equals(this.getId()))
			return true;
		return false;
	}

	/* Getters and setters */

	@Override
	public String getArtist() {
		return artist;
	}

	public void setArtist(final String artist) {
		this.artist = artist;
	}

	@Override
	public String getSong() {
		return this.song;
	}

	public void setSong(final String song) {
		this.song = song;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public static void setYoutubeApiKey(final String key) {
		YOUTUBE_API_KEY = key;
	}

}
