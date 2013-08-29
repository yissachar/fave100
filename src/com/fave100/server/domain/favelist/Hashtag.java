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
	// Same as id, but case sensitive for display
	private String name;
	private Ref<AppUser> createdBy;
	private Date dateCreated;
	private List<FaveItem> list = new ArrayList<FaveItem>();
	// A constantly regenerated sampling of up to 9 users with this list
	// Since this is constantly regenerated, we can store denormalized data, instead of reference to actual entity
	private List<String> sampledUsersNames = new ArrayList<>();
	private List<String> sampledUsersAvatars = new ArrayList<>();
	// Total number of lists with this hashtag
	private int listCount;

	@SuppressWarnings("unused")
	private Hashtag() {
	}

	public Hashtag(final String name, final String createdBy) {
		this.id = name.toLowerCase();
		this.name = name;
		this.setCreatedBy(Ref.create(Key.create(AppUser.class, createdBy)));
		setDateCreated(new Date());
	}

	public static Hashtag findHashtag(final String id) {
		return ofy().load().type(Hashtag.class).id(id).get();
	}

	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

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

	public List<String> getSampledUsersNames() {
		return sampledUsersNames;
	}

	public void setSampledUsersNames(final List<String> sampledUsersNames) {
		this.sampledUsersNames = sampledUsersNames;
	}

	public List<String> getSampledUsersAvatars() {
		return sampledUsersAvatars;
	}

	public void setSampledUsersAvatars(final List<String> sampledUsersAvatars) {
		this.sampledUsersAvatars = sampledUsersAvatars;
	}

	public int getListCount() {
		return listCount;
	}

	public void setListCount(final int listCount) {
		this.listCount = listCount;
	}

}
