package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Fave100MasterList {

	@IgnoreSave public static final String CURRENT_MASTER = "currentMaster";

	@Id private String id;
	@Load private List<Ref<Song>> songList = new ArrayList<Ref<Song>>();

	@SuppressWarnings("unused")
	private Fave100MasterList(){}

	public Fave100MasterList(final String id) {
		this.id = id;
	}


	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public List<Ref<Song>> getSongList() {
		return songList;
	}

	public void setSongList(final List<Ref<Song>> songList) {
		this.songList = songList;
	}


}
