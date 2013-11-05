package com.fave100.shared.requestfactory;

import com.fave100.server.domain.UserListResult;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(UserListResult.class)
public interface UserListResultProxy extends ValueProxy {
	String getUserName();

	String getListName();

	String getAvatar();
}