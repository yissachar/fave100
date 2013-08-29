package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.favelist.Hashtag;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(Hashtag.class)
public interface HashtagProxy extends ValueProxy {
	String getName();

	List<String> getSampledUsersNames();

	List<String> getSampledUsersAvatars();

	int getListCount();
}
