package com.fave100.shared.requestfactory;

import com.fave100.server.domain.favelist.FaveItem;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(FaveItem.class)
public interface FaveItemProxy extends ValueProxy {
	String getSong();
	String getArtist();
	String getWhyline();
}
