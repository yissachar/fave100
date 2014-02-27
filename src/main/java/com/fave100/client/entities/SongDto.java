package com.fave100.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fave100.shared.FaveItemInterface;

public class SongDto implements FaveItemInterface {

	@JsonProperty("id") private String id;
	@JsonProperty("song") private String song;
	@JsonProperty("artist") private String artist;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getArtist() {
		return artist;
	}

	@Override
	public String getSong() {
		return song;
	}

}
