package com.fave100.client.entities;

import java.util.List;

public class SearchResult {

	private List<SongDto> results;

	private int total;

	/* Getters and Setters */

	public List<SongDto> getResults() {
		return results;
	}

	public void setResults(List<SongDto> results) {
		this.results = results;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}