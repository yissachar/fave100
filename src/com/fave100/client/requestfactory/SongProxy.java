package com.fave100.client.requestfactory;

import com.fave100.client.pages.search.SongInterface;
import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(Song.class)
public interface SongProxy extends EntityProxy, SongInterface {
	String getId();
	Integer getVersion();

	String getWhyline();
	int getWhylineScore();

	int getResultCount();
}
