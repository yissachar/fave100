package com.fave100.server.domain.favelist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class Hashtag {

	@IgnoreSave public static final int MAX_STORED_LIST_COUNTS = 84;

	@Id private String id;
	// Same as id, but case sensitive for display
	private String name;
	private Ref<AppUser> createdBy;
	private Date dateCreated;
	private List<FaveItem> list = new ArrayList<FaveItem>();
	@Index private double zscore = 0;
	List<Integer> slidingListCount = new ArrayList<>();

	@SuppressWarnings("unused")
	private Hashtag() {
	}

	public Hashtag(final String name, final String createdBy) {
		this.id = name.toLowerCase();
		this.name = name;
		this.setCreatedBy(Ref.create(Key.create(AppUser.class, createdBy)));
		setDateCreated(new Date());
	}

	public void addListCount(int listCount) {
		getSlidingListCount().add(listCount);
		while (getSlidingListCount().size() > MAX_STORED_LIST_COUNTS) {
			getSlidingListCount().remove(0);
		}
	}

	/* Getters and Setters */

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
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

	public double getZscore() {
		return zscore;
	}

	public void setZscore(double zscore) {
		this.zscore = zscore;
	}

	public List<Integer> getSlidingListCount() {
		return slidingListCount;
	}

	public void setSlidingListCount(List<Integer> slidingListCount) {
		this.slidingListCount = slidingListCount;
	}

}
