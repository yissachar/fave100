package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A temporary class to hold all featured lists, until Trending Lists are turned back on.
 * There should only ever be one instance of this stored in the database.
 * 
 * @author yissachar.radcliffe
 *
 */
@Entity
public class FeaturedLists {

	@Id private String id;
	private List<String> lists = new ArrayList<String>();

	@SuppressWarnings("unused")
	private FeaturedLists() {
	}

	public FeaturedLists(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<String> getLists() {
		return lists;
	}

	public void setLists(List<String> lists) {
		this.lists = lists;
	}

}
