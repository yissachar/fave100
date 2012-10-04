package com.fave100.client.pages.search;

public class MusicbrainzResult {
	
	private String trackName;
	private String artistName;
	private String releaseDate;
	
	public MusicbrainzResult(final String trackName, final String artistName, final String releaseDate) {
		this.trackName = trackName;
		this.artistName = artistName;
		this.releaseDate = releaseDate;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(final String trackName) {
		this.trackName = trackName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final String releaseDate) {
		this.releaseDate = releaseDate;
	}

}
