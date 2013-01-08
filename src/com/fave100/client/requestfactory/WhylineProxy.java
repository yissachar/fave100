package com.fave100.client.requestfactory;

import com.fave100.server.domain.Whyline;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(Whyline.class)
public interface WhylineProxy extends EntityProxy{
	Integer getVersion();

	String getWhyline();
	String getUsername();
}
