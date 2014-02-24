package com.fave100.client.entities;

import com.fave100.shared.FaveItemInterface;

public class SongDto implements FaveItemInterface {

	private String id;
	private String song;
	private String artist;

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
