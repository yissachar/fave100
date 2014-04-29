package com.fave100.server.domain;

public class WhylineEdit {

	private String listName;
	private String songId;
	private String whyline;

	public WhylineEdit() {
	}

	public WhylineEdit(String listName, String songId, String whyline) {
		this.listName = listName;
		this.songId = songId;
		this.whyline = whyline;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getSongId() {
		return songId;
	}

	public void setSongId(String songId) {
		this.songId = songId;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(String whyline) {
		this.whyline = whyline;
	}
}
