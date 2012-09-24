package com.fave100.client.requestfactory;

import com.fave100.server.domain.AppUser;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(AppUser.class)
public interface AppUserProxy extends EntityProxy{	
	Integer getVersion();
	
	String getUsername();
	String getEmail();
	String getAvatarImage();
}
