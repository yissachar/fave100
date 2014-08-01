package com.fave100.server.domain;

public class CursoredSearchResult {

	private String cursor;

	private StringResultCollection searchResults;

	public CursoredSearchResult() {
	}

	public CursoredSearchResult(String cursor, StringResultCollection searchResults) {
		this.cursor = cursor;
		this.searchResults = searchResults;
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public StringResultCollection getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(StringResultCollection searchResults) {
		this.searchResults = searchResults;
	}

}
