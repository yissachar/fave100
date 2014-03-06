package com.fave100.server.domain;

import java.util.List;

public class StringResultCollection {

	private List<StringResult> items;

	public StringResultCollection(List<StringResult> items) {
		this.items = items;
	}

	public List<StringResult> getItems() {
		return items;
	}

	public void setItems(List<StringResult> items) {
		this.items = items;
	}

}
