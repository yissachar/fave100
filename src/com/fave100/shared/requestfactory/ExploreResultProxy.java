package com.fave100.shared.requestfactory;

import com.fave100.server.domain.ExploreResult;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(ExploreResult.class)
public interface ExploreResultProxy extends ValueProxy, FaveItemProxy, WhylineProxy {

	String getAvatar();

}
