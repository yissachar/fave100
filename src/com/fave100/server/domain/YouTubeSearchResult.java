package com.fave100.server.domain;

public class YouTubeSearchResult {

	private String videoId;
	private String thumbnail;

	@SuppressWarnings("unused")
	private YouTubeSearchResult() {
	}

	public YouTubeSearchResult(final String videoId, final String thumbnail) {
		this.videoId = videoId;
		this.thumbnail = thumbnail;
	}

	/* Getters and Setters */

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(final String videoId) {
		this.videoId = videoId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(final String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
