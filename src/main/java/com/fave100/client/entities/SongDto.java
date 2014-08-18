package com.fave100.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SongDto {

	@JsonProperty("id") private String id;
	@JsonProperty("song") private String song;
	@JsonProperty("artist") private String artist;
	@JsonProperty("cover_art") private String coverArt;

	public String getId() {
		return id;
	}

	public String getArtist() {
		return artist;
	}

	public String getSong() {
		return song;
	}

	public String getCoverArt() {
		return coverArt;
	}
}
