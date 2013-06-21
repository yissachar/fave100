package com.fave100.shared.requestfactory;

import com.fave100.server.domain.appuser.UserInfo;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(UserInfo.class)
public interface UserInfoProxy extends ValueProxy {
	String getEmail();

	void setEmail(String email);

	boolean isFollowingPrivate();

	void setFollowingPrivate(boolean priv);
}