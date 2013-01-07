package com.fave100.client.requestfactory;

import com.fave100.server.domain.favelist.FaveList;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(FaveList.class)
public interface FaveListProxy extends EntityProxy {
	String getId();
	Integer getVersion();
}
