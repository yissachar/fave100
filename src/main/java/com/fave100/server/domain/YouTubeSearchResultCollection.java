package com.fave100.server.domain;

import java.util.List;

public class YouTubeSearchResultCollection {

	private List<YouTubeSearchResult> items;

	public YouTubeSearchResultCollection(List<YouTubeSearchResult> items) {
		this.setItems(items);
	}

	public List<YouTubeSearchResult> getItems() {
		return items;
	}

	public void setItems(List<YouTubeSearchResult> items) {
		this.items = items;
	}

}
