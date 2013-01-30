package com.fave100.client.requestfactory;

import com.fave100.server.domain.Song;
import com.fave100.shared.SongInterface;
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
