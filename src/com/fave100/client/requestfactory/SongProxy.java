package com.fave100.client.requestfactory;

import com.fave100.server.domain.Song;
import com.fave100.shared.SongInterface;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(Song.class)
public interface SongProxy extends EntityProxy, SongInterface {
	Integer getVersion();

	int getResultCount();
}
