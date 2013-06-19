package com.fave100.client;

import java.util.List;

import com.fave100.client.RequestCache.RequestType;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.UserFollowedEvent;
import com.fave100.client.events.UserUnfollowedEvent;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class CurrentUser implements AppUserProxy {

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private AppUserProxy appUser;
	private String avatar = "";
	private List<AppUserProxy> following;

	@Inject
	public CurrentUser(final EventBus eventBus, final ApplicationRequestFactory requestFactory, final RequestCache requestCache) {
		_eventBus = eventBus;
		_requestFactory = requestFactory;

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setAppUser(event.getUser());
						if (appUser != null) {
							avatar = appUser.getAvatarImage();

							final AsyncCallback<List<AppUserProxy>> followingReq = new AsyncCallback<List<AppUserProxy>>() {
								@Override
								public void onFailure(final Throwable caught) {
									// Don't care
								}

								@Override
								public void onSuccess(final List<AppUserProxy> result) {
									following = result;
								}

							};
							requestCache.getFollowingUsers(followingReq);
						}
						else {
							// User not logged in, clear stale user request cache
							requestCache.clearRequestCache(RequestType.FOLLOWING_USERS);
						}
					}
				});
	}

	public boolean isLoggedIn() {
		return appUser != null;
	}

	public void setAppUser(final AppUserProxy appUser) {
		this.appUser = appUser;
	}

	public void setAvatar(final String url) {
		avatar = url;
	}

	public List<AppUserProxy> getFollowing() {
		return following;
	}

	public boolean isFollowingUser(final AppUserProxy user) {
		if (!isLoggedIn() || getFollowing() == null)
			return false;

		return getFollowing().contains(user);
	}

	public void followUser(final AppUserProxy user) {
		if (isFollowingUser(user))
			return;

		// Add to client
		getFollowing().add(user);

		// Add to server
		final Request<Void> starReq = _requestFactory.appUserRequest().followUser(user.getUsername());
		starReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_eventBus.fireEvent(new UserFollowedEvent(user));
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				// Roll back
				getFollowing().remove(user);
				final String errorMsg = failure.getMessage();
				_eventBus.fireEvent(new UserUnfollowedEvent(user, errorMsg));
			}
		});
	}

	public void unfollowUser(final AppUserProxy user) {
		if (!isFollowingUser(user))
			return;

		// Remove from client
		getFollowing().remove(user);

		_eventBus.fireEvent(new UserUnfollowedEvent(user));

		// Remove from server
		final Request<Void> unfollowReq = _requestFactory.appUserRequest().unfollowUser(user.getUsername());
		unfollowReq.fire();
	}

	// Needed for RequestFactory
	@Override
	public EntityProxyId<?> stableId() {
		return appUser.stableId();
	}

	@Override
	public Integer getVersion() {
		return appUser.getVersion();
	}

	@Override
	public String getUsername() {
		return appUser.getUsername();
	}

	@Override
	public String getAvatarImage() {
		return avatar;
	}

	@Override
	public boolean equals(final Object obj) {
		return appUser.equals(obj);
	}
}
