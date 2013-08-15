package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Hashtag {

	@Id private String id;
	private Ref<AppUser> createdBy;
	private Date dateCreated;
	private List<FaveItem> list = new ArrayList<FaveItem>();

	@SuppressWarnings("unused")
	private Hashtag() {
	}

	public Hashtag(final String id, final String createdBy) {
		this.id = id;
		this.setCreatedBy(Ref.create(Key.create(AppUser.class, createdBy)));
		setDateCreated(new Date());
	}

	public static Hashtag findHashtag(final String id) {
		return ofy().load().type(Hashtag.class).id(id).get();
	}

	/* Getters and Setters */

	public Ref<AppUser> getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final Ref<AppUser> createdBy) {
		this.createdBy = createdBy;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(final Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public List<FaveItem> getList() {
		return list;
	}

	public void setList(final List<FaveItem> list) {
		this.list = list;
	}

}
