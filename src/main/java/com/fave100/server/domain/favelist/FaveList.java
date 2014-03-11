package com.fave100.server.domain.favelist;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class FaveList {

	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	@Index private String hashtagId;
	private List<FaveItem> list = new ArrayList<FaveItem>();

	@SuppressWarnings("unused")
	private FaveList() {
	}

	public FaveList(final String username, final String hashtag) {
		this.id = username.toLowerCase() + FaveListDao.SEPERATOR_TOKEN + hashtag.toLowerCase();
		this.user = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		setHashtag(hashtag);
	}

	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Ref<AppUser> getUser() {
		return user;
	}

	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(final String hashtag) {
		this.hashtag = hashtag;
		this.hashtagId = hashtag.toLowerCase();
	}

	public List<FaveItem> getList() {
		return list;
	}

	public String getHashtagId() {
		return hashtagId;
	}

	public void setHashtagId(String hashtagId) {
		this.hashtagId = hashtagId;
	}

}
