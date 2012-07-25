package com.fave100.client.requestfactory;

import com.fave100.client.pages.myfave100.SuggestionResult;
import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

@ProxyFor(Song.class)
public interface SongProxy extends EntityProxy, SuggestionResult {
	Long getId();
	void setId(Long id);
	Integer getVersion();
	
	/*String getTitle();
	void setTitle(String title);
	
	String getArtistName();
	void setArtist(String artistName);
	
	Integer getReleaseYear();
	void setReleaseYear(Integer date);
	
	String getItemURL();
	void setItemURL(String itemURL);*/

}
