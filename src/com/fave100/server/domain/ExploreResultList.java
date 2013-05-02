package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

@Entity
public class ExploreResultList {

	@IgnoreSave public static final String CURRENT_LIST = "currentList";

	@Id private String id;
	private List<ExploreResult> list = new ArrayList<>();

	public ExploreResultList() {
	}

	public ExploreResultList(final String id) {
		this.id = id;
	}

	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public List<ExploreResult> getList() {
		return list;
	}

	public void setList(final List<ExploreResult> list) {
		this.list = list;
	}

}
