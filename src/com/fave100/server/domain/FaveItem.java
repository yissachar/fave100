package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.DAO;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

@Entity
public class FaveItem extends DatastoreObject{
		
	private Long appUser;
	@Unindexed
	private String title;
	@Unindexed
	private String artist;
	@Unindexed 
	private Integer releaseYear;
	@Unindexed
	private String itemURL;
	private String whyline;
	
	public static final Objectify ofy() {
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static FaveItem findFaveItem(Long id) {
		return ofy().get(FaveItem.class, id);
	}
	
	public static void removeFaveItem(Long id) {
		ofy().delete(FaveItem.class, id);
	}
	
	public static List<FaveItem> getAllFaveItemsForUser(Long appUser) {
		List<FaveItem> allFaveItemsForUser = new ArrayList<FaveItem>();
		return ofy().query(FaveItem.class).filter("appUser", appUser).list();
	}
	
	public FaveItem persist() {
		ofy().put(this);
		return this;
	}
	
	public void remove() {
		ofy().delete(this);
	}
	
	/*Getters and Setters */

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getItemURL() {
		return itemURL;
	}

	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;
	}

	public Long getAppUser() {
		return appUser;
	}

	public void setAppUser(Long appUser) {
		this.appUser = appUser;
	}

	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(String whyline) {
		this.whyline = whyline;
	}

}
