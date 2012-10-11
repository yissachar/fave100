package com.fave100.client.pages.search;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MusicbrainzResult implements Serializable {
	
	private String mbid;
	private String trackName;
	private String artistName;
	private String releaseDate;
	
	public MusicbrainzResult() {
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

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

}
