package com.fave100.shared.requestfactory;

import com.fave100.server.domain.Whyline;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(Whyline.class)
public interface WhylineProxy extends ValueProxy {

	String getWhyline();

	String getUsername();

	String getAvatar();
}
