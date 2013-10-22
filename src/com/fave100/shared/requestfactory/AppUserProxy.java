package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserLocator;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = AppUser.class, locator = AppUserLocator.class)
public interface AppUserProxy extends EntityProxy {
	Integer getVersion();

	String getUsername();

	String getAvatarImage();

	List<String> getHashtags();
}
