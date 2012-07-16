package com.fave100.client.requestfactory;

import com.fave100.server.domain.FaveItem;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

@ProxyFor(FaveItem.class)
public interface FaveItemProxy extends EntityProxy {

	Long getId();
	Integer getVersion();
	
	String getTitle();
	void setTitle(String title);
}
