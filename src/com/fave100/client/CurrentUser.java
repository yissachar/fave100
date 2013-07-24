package com.fave100.client;

import java.util.List;

import com.fave100.client.RequestCache.RequestType;
import com.fave100.client.events.favelist.FaveItemAddedEvent;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.FaveListRequest;
import com.fave100.shared.requestfactory.FollowingResultProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class CurrentUser implements AppUserProxy {

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private PlaceManager _placeManager;
	private AppUserProxy appUser;
	private String avatar = "";
	private List<FaveItemProxy> faveList;
	private FollowingResultProxy followingResult;
	private boolean fullListRetrieved = false;

	@Inject
	public CurrentUser(final EventBus eventBus, final ApplicationRequestFactory requestFactory, final PlaceManager placeManager, final RequestCache requestCache) {
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_placeManager = placeManager;

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
		return followingResult == null ? null : followingResult.getFollowing();
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

	public void addSong(final String songID, final String song, final String artist) {

		final FaveListRequest faveListRequest = _requestFactory.faveListRequest();
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(Constants.DEFAULT_HASHTAG, songID);

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Song added");
				final FaveItemProxy item = new FaveItemProxy() {

					@Override
					public String getWhyline() {
						return null;
					}

					@Override
					public String getSongID() {
						return songID;
					}

					@Override
					public String getSong() {
						return song;
					}

					@Override
					public String getArtist() {
						return artist;
					}
				};
				// Ensure local list in sync				
				faveList.add(item);
				_eventBus.fireEvent(new FaveItemAddedEvent(item));
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(null));
					_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
				}
				else if (failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
					Notification.show("You cannot have more than 100 songs in list");
				}
				else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
					Notification.show("The song is already in your list");
				}
				else {
					// Catch-all
					Notification.show("Error: Could not add song");
				}
			}
		});
	}

	// Needed for RequestFactory
	@Override
	public EntityProxyId<?> stableId() {
		return (appUser == null) ? null : appUser.stableId();
	}

	@Override
	public Integer getVersion() {
		return (appUser == null) ? null : appUser.getVersion();
	}

	@Override
	public String getUsername() {
		return (appUser == null) ? null : appUser.getUsername();
	}

	@Override
	public String getAvatarImage() {
		return avatar;
	}

	@Override
	public boolean equals(final Object obj) {
		return (appUser == null) ? false : appUser.equals(obj);
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
