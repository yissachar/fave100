package com.fave100.client.requestfactory;

import com.fave100.server.domain.FaveItem;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

@ProxyFor(FaveItem.class)
public interface FaveItemProxy extends EntityProxy {

	Long getId();
	void setId(Long id);
	Integer getVersion();
	
	Long getAppUser();
	void setAppUser(Long appUser);
	
	String getTitle();
	void setTitle(String title);
	
	String getArtist();
	void setArtist(String artist);
	
	Integer getReleaseYear();
	void setReleaseYear(Integer date);
	
	String getItemURL();
	void setItemURL(String itemURL);
}
