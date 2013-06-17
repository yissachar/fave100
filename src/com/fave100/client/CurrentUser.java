package com.fave100.client;

import java.util.List;

import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.ListStarredEvent;
import com.fave100.client.events.ListUnstarredEvent;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.TooManyStarredListsException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FavelistIDProxy;
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
	private List<FavelistIDProxy> starredLists;

	@Inject
	public CurrentUser(final EventBus eventBus, final ApplicationRequestFactory requestFactory) {
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

							final Request<List<FavelistIDProxy>> starredListReq = requestFactory.appUserRequest().getStarredListsForCurrentUser();
							starredListReq.fire(new Receiver<List<FavelistIDProxy>>() {
								@Override
								public void onSuccess(final List<FavelistIDProxy> results) {
									starredLists = results;
								}
							});
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

	public List<FavelistIDProxy> getStarredLists() {
		return starredLists;
	}

	public boolean isStarredList(final String username, final String hashtag) {
		if (!isLoggedIn() || getStarredLists() == null)
			return false;

		return indexOfStarredList(username, hashtag) != -1;
	}

	public int indexOfStarredList(final String username, final String hashtag) {
		for (int i = 0; i < starredLists.size(); i++) {
			final FavelistIDProxy starredList = starredLists.get(i);
			if (starredList.getUsername().equals(username) && starredList.getHashtag().equals(hashtag)) {
				return i;
			}
		}
		return -1;
	}

	public void starList(final String username, final String hashtag) {
		if (isStarredList(username, hashtag))
			return;

		// Add to client
		getStarredLists().add(new FavelistIDProxy() {
			@Override
			public String getUsername() {
				return username;
			}

			@Override
			public String getHashtag() {
				return hashtag;
			}
		});

		// Add to server
		final Request<Void> starReq = _requestFactory.appUserRequest().starList(username, hashtag);
		starReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_eventBus.fireEvent(new ListStarredEvent());
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				// Roll back
				getStarredLists().remove(indexOfStarredList(username, hashtag));
				String errorMsg = failure.getMessage();
				if (failure.getExceptionType().equals(TooManyStarredListsException.class.getName()))
					errorMsg = "You can only have " + Constants.MAX_STARRED_LISTS + " starred lists";
				_eventBus.fireEvent(new ListUnstarredEvent(errorMsg));
			}
		});
	}

	public void unstarList(final String username, final String hashtag) {
		if (!isStarredList(username, hashtag))
			return;

		// Remove from client
		getStarredLists().remove(indexOfStarredList(username, hashtag));

		_eventBus.fireEvent(new ListUnstarredEvent());

		// Remove from server
		final Request<Void> unstarReq = _requestFactory.appUserRequest().unstarList(username, hashtag);
		unstarReq.fire();
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
