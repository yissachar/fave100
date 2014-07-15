package com.fave100.client.entities;


public class ItunesSearchResult {

	private String trackName;

	private String artistName;

	private String trackViewUrl;

	private String artworkUrl60;

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getTrackViewUrl() {
		return trackViewUrl;
	}

	public void setTrackViewUrl(String trackViewUrl) {
		this.trackViewUrl = trackViewUrl;
	}

	public String getArtworkUrl60() {
		return artworkUrl60;
	}

	public void setArtworkUrl60(String artworkUrl60) {
		this.artworkUrl60 = artworkUrl60;
	}
}
