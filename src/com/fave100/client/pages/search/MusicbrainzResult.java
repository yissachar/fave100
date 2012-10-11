package com.fave100.client.pages.search;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MusicbrainzResult implements Serializable, SongInterface {
	
	private String mbid;
	private String trackName;
	private String artistName;
	private String releaseDate;
	private String coverArtUrl;
	private String primaryGenreName;
	
	public MusicbrainzResult() {
	}

	@Override
	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(final String trackName) {
		this.trackName = trackName;
	}

	@Override
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	@Override
	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(final String releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String getMbid() {
		return mbid;
	}

	public void setMbid(final String mbid) {
		this.mbid = mbid;
	}

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public void setCoverArtUrl(final String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
	}

	public String getPrimaryGenreName() {
		return primaryGenreName;
	}

	public void setPrimaryGenreName(String primaryGenreName) {
		this.primaryGenreName = primaryGenreName;
	}

}
