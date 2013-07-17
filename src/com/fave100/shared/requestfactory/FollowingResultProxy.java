package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.appuser.FollowingResult;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(FollowingResult.class)
public interface FollowingResultProxy extends ValueProxy {

	List<AppUserProxy> getFollowing();

	boolean isMore();

}
