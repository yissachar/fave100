package com.fave100.shared.requestfactory;

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.SongInterface;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(FaveItem.class)
public interface FaveItemProxy extends ValueProxy, SongInterface {
	@Override
	String getSong();

	@Override
	String getArtist();

	// This is just a pointer to songID
	@Override
	String getId();

	String getWhyline();
}
