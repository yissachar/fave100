package com.fave100.server.api;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.fave100.server.SessionAttributes;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.exceptions.NotLoggedInException;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Provider
public class UserProvider implements Injectable<AppUser>, InjectableProvider<LoggedInUser, Type> {

	private HttpServletRequest request;

	public UserProvider(@Context HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Injectable<AppUser> getInjectable(ComponentContext ic, LoggedInUser a, Type c) {
		if (c.equals(AppUser.class))
			return this;

		return null;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public AppUser getValue() {
		AppUser user = AppUserDao.findAppUser((String)request.getSession().getAttribute(SessionAttributes.AUTH_USER));

		if (user == null) {
			throw new NotLoggedInException();
		}

		return user;
	}

}
