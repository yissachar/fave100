package com.fave100.client;

import java.util.List;

import com.fave100.client.RequestCache.RequestType;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.UserFollowedEvent;
import com.fave100.client.events.UserUnfollowedEvent;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.FollowingResultProxy;
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
	private List<FaveItemProxy> faveList;
	private FollowingResultProxy followingResult;
	private boolean fullListRetrieved = false;

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

							final AsyncCallback<FollowingResultProxy> followingReq = new AsyncCallback<FollowingResultProxy>() {
								@Override
								public void onFailure(final Throwable caught) {
									// Don't care
								}

								@Override
								public void onSuccess(final FollowingResultProxy result) {
									followingResult = result;
								}

							};
							requestCache.getFollowingForCurrentUser(getUsername(), followingReq);
						}
						else {
							// User not logged in

							// Clear stale user request cache
							requestCache.clearRequestCache(RequestType.FOLLOWING_CURRENT_USER);

							// Clear all state
							appUser = null;
							avatar = "";
							faveList = null;
							followingResult = null;
							fullListRetrieved = false;
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
		return followingResult.getFollowing();
	}

	public void followUser(final AppUserProxy user) {
		// Add to client
		if (!getFollowing().contains(user))
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
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(null));
				}
			}
		});
	}

	public void unfollowUser(final AppUserProxy user) {
		// Remove from client
		getFollowing().remove(user);

		// Remove from server
		final Request<Void> unfollowReq = _requestFactory.appUserRequest().unfollowUser(user.getUsername());
		unfollowReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_eventBus.fireEvent(new UserUnfollowedEvent(user));
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(null));
				}
			}
		});
	}

	public void addMoreFollowing(final List<AppUserProxy> users, final Boolean isMore) {
		followingResult.getFollowing().addAll(users);
		setFullListRetrieved(!isMore);
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

	public List<FaveItemProxy> getFaveList() {
		return faveList;
	}

	public void setFaveList(final List<FaveItemProxy> faveList) {
		this.faveList = faveList;
	}

	public boolean isFullListRetrieved() {
		return fullListRetrieved;
	}

	public void setFullListRetrieved(final boolean fullListRetrieved) {
		this.fullListRetrieved = fullListRetrieved;
	}

	public AppUserProxy getAppUser() {
		return appUser;
	}

}
