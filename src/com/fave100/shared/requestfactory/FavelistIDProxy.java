package com.fave100.shared.requestfactory;

import com.fave100.server.domain.favelist.FaveListID;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(FaveListID.class)
public interface FavelistIDProxy extends ValueProxy {

	String getUsername();

	String getHashtag();
}
