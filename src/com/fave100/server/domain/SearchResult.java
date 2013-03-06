package com.fave100.server.domain;

import java.util.List;

public class SearchResult {

	private List<Song> results;
	// The number of results
	private int total;

	@SuppressWarnings("unused")
	private SearchResult(){}

	public SearchResult(final List<Song> results, final int total) {
		this.results = results;
		this.total = total;
	}

	/* Getters and Setters */

	public int getTotal() {
		return total;
	}

	public void setTotal(final int total) {
		this.total = total;
	}


	public List<Song> getResults() {
		return results;
	}


	public void setResults(final List<Song> results) {
		this.results = results;
	}

}
