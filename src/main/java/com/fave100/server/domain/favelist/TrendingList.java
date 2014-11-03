package com.fave100.server.domain.favelist;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class TrendingList {

	@Id private String id;
	private List<FaveItem> items = new ArrayList<FaveItem>();

	@SuppressWarnings("unused")
	private TrendingList() {
	}

	public TrendingList(String id) {
		this.id = id;
	}

	public TrendingList(String id, List<FaveItem> items) {
		this(id);
		setItems(items);
	}

	public String getId() {
		return id;
	}

	public List<FaveItem> getItems() {
		return items;
	}

	public void setItems(List<FaveItem> items) {
		this.items = items;
	}
}
