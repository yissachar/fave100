package com.fave100.server.domain;

import java.util.List;

/*
 * Workaround to Google Cloud Endpoints limitation of only returning POJOs or POJO collections
 */
public class StringCollection {

	private List<String> items;

	public StringCollection(List<String> items) {
		setItems(items);
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

}
