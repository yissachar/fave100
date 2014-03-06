package com.fave100.server.domain;

import java.util.List;

public class WhylineCollection {

	private List<Whyline> items;

	public WhylineCollection(List<Whyline> items) {
		this.setItems(items);
	}

	public List<Whyline> getItems() {
		return items;
	}

	public void setItems(List<Whyline> items) {
		this.items = items;
	}

}
