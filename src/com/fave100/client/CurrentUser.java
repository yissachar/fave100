package com.fave100.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.client.RequestCache.RequestType;
import com.fave100.client.events.favelist.AddSongListsSelectedEvent;
import com.fave100.client.events.favelist.FaveItemAddedEvent;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.generated.entities.AppUserDto;
import com.fave100.client.generated.entities.FaveItemDto;
import com.fave100.client.generated.entities.FollowingResultDto;
import com.fave100.client.generated.services.AppUserService;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.FaveListRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class CurrentUser extends AppUserDto {

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private PlaceManager _placeManager;
	private DispatchAsync _dispatcher;
	private AppUserService _appUserService;
	private AppUserDto appUser;
	private String avatar = "";
	private Map<String, List<FaveItemDto>> faveLists = new HashMap<String, List<FaveItemDto>>();
	private List<String> _hashtags = new ArrayList<String>();
	private String _currentHashtag = Constants.DEFAULT_HASHTAG;
	private FollowingResultDto followingResult;
	private boolean fullListRetrieved = false;

	@Inject
	public CurrentUser(final EventBus eventBus, final ApplicationRequestFactory requestFactory, final PlaceManager placeManager, final RequestCache requestCache,
						final DispatchAsync dispatcher, final AppUserService appUserService) {
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_appUserService = appUserService;

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setAppUser(event.getUser());
						if (appUser != null) {
							avatar = appUser.getAvatarImage();
							_hashtags.clear();
							_hashtags.add(Constants.DEFAULT_HASHTAG);
							_hashtags.addAll(appUser.getHashtags());

							final AsyncCallback<FollowingResultDto> followingReq = new AsyncCallback<FollowingResultDto>() {
								@Override
								public void onFailure(final Throwable caught) {
									// TODO: What happens if fail?
								}

								@Override
								public void onSuccess(final FollowingResultDto result) {
									followingResult = result;
									fullListRetrieved = !result.isMore();
								}

							};
							requestCache.getFollowingForCurrentUser(getUsername(), followingReq);
						}
						else {
							// User not logged in
							resetState();

							// Clear stale user request cache
							requestCache.clearRequestCache(RequestType.FOLLOWING_CURRENT_USER);
						}
					}
				});

		AddSongListsSelectedEvent.register(eventBus, new AddSongListsSelectedEvent.Handler() {
			@Override
			public void onAddSongListsSelected(AddSongListsSelectedEvent event) {
				for (String listName : event.getSelectedLists()) {
					addSong(event.getSongId(), listName, event.getSongName(), event.getSongArtist());
				}
			}
		});
	}

	private void resetState() {
		// Clear all state
		appUser = null;
		avatar = "";
		faveLists = new HashMap<String, List<FaveItemDto>>();
		followingResult = null;
		fullListRetrieved = false;
		_currentHashtag = Constants.DEFAULT_HASHTAG;
		_hashtags = new ArrayList<String>();
	}

	public boolean isLoggedIn() {
		return appUser != null;
	}

	public void setAppUser(final AppUserDto appUser) {
		this.appUser = appUser;
	}

	public void setAvatar(final String url) {
		avatar = url;
	}

	public void logout() {
		final AppUserRequest appUserRequest = _requestFactory.appUserRequest();
		final Request<Void> logoutReq = appUserRequest.logout();
		logoutReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				_eventBus.fireEvent(new CurrentUserChangedEvent(null));
				Notification.show("Logged out successfully");
				_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.lists).build());
			}
		});
	}

	public List<AppUserDto> getFollowing() {
		return followingResult == null ? null : followingResult.getFollowing();
	}

	public void followUser(final AppUserDto user) {
		// Not logged in, redirect to login
		if (!isLoggedIn()) {
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
			return;
		}

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

	public void unfollowUser(final AppUserDto user) {
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

	public void addMoreFollowing(final List<AppUserDto> users, final Boolean isMore) {
		followingResult.getFollowing().addAll(users);
		setFullListRetrieved(!isMore);
	}

	public void addSong(final String songID, final String song, final String artist) {
		addSong(songID, _currentHashtag, song, artist);
	}

	public void addSong(final String songID, final String hashtag, final String song, final String artist) {

		final FaveListRequest faveListRequest = _requestFactory.faveListRequest();
		final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(hashtag, songID);

		addReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				Notification.show("Song added");
				final FaveItemDto item = new FaveItemDto();
				item.setSong(song);
				item.setArtist(artist);
				item.setSongID(songID);
				item.setId(songID);

				// Ensure local list in sync	
				if (getFaveLists().get(hashtag) != null) {
					getFaveLists().get(hashtag).add(item);
					_eventBus.fireEvent(new FaveItemAddedEvent(item));
				}
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

	public void addFaveList(String name) {
		if (getHashtags().contains(name) || name.equals(Constants.DEFAULT_HASHTAG))
			return;

		final String listName = name;
		Request<Void> addFaveListReq = _requestFactory.faveListRequest().addFaveListForCurrentUser(listName);
		addFaveListReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(Void response) {
				getHashtags().add(listName);
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(ListPresenter.LIST_PARAM, listName)
						.with(ListPresenter.USER_PARAM, getUsername())
						.build());
			}
		});
	}

	public void deleteList(String name) {
		getHashtags().remove(name);
		faveLists.remove(name);
	}

	public List<FaveItemDto> getFaveList() {
		return faveLists.get(_currentHashtag);
	}

	public void setFaveList(final List<FaveItemDto> favelist) {
		faveLists.put(_currentHashtag, favelist);
	}

	public void addHashtag(final String hashtag) {
		_hashtags.add(hashtag);
	}

	@Override
	public int getVersion() {
		return (appUser == null) ? null : appUser.getVersion();
	}

	@Override
	public String getUsername() {
		return (appUser == null) ? null : appUser.getUsername();
	}

	@Override
	public List<String> getHashtags() {
		return _hashtags;
	}

	@Override
	public String getAvatarImage() {
		return avatar;
	}

	@Override
	public boolean equals(Object obj) {
		if (appUser == null || obj == null) {
			return false;
		}
		else if (appUser == obj || appUser.equals(obj) || this.getUsername().equals(((AppUserProxy)obj).getUsername())) {
			return true;
		}
		return false;
	}

	public Map<String, List<FaveItemDto>> getFaveLists() {
		return faveLists;
	}

	public void setFaveLists(final Map<String, List<FaveItemDto>> faveLists) {
		this.faveLists = faveLists;
	}

	public boolean isFullListRetrieved() {
		return fullListRetrieved;
	}

	public void setFullListRetrieved(final boolean fullListRetrieved) {
		this.fullListRetrieved = fullListRetrieved;
	}

	public AppUserDto getAppUser() {
		return appUser;
	}

	public String getCurrentHashtag() {
		return _currentHashtag;
	}

	public void setCurrentHashtag(final String currentHashtag) {
		this._currentHashtag = currentHashtag;
	}

}
