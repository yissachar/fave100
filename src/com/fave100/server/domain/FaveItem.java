package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.DAO;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class FaveItem extends DatastoreObject{
	
	private String title;
	
	public static final Objectify ofy() {
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static FaveItem findFaveItem(Long id) {
		return ofy().get(FaveItem.class, id);
	}
	
	public static List<FaveItem> getAllFaveItemsForUser() {
		//TODO: restrict items by user
		List<FaveItem> allFaveItemsForUser = new ArrayList<FaveItem>();
		Query<FaveItem> q = ofy().query(FaveItem.class);
		for(FaveItem faveItem : q) {
			allFaveItemsForUser.add(faveItem);
		}
		return allFaveItemsForUser;
	}
	
	public FaveItem persist() {
		ofy().put(this);
		return this;
	}
	
	public void remove() {
		ofy().delete(this);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
