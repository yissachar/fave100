package com.fave100.client.requestfactory;

import com.fave100.client.pages.myfave100.SuggestionResult;
import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(Song.class)
public interface SongProxy extends EntityProxy, SuggestionResult {
	Long getId();
	Integer getVersion();
	
	String getWhyline();
}
