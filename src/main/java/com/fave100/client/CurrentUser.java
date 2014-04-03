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
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.FollowingResult;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class CurrentUser extends AppUser {

	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private AppUser appUser;
	private String avatar = "";
	private Map<String, List<FaveItem>> faveLists = new HashMap<String, List<FaveItem>>();
	private List<String> _hashtags = new ArrayList<String>();
	private String _currentHashtag = Constants.DEFAULT_HASHTAG;
	private FollowingResult followingResult;
	private boolean fullListRetrieved = false;

	@Inject
	public CurrentUser(final EventBus eventBus, final PlaceManager placeManager, final RequestCache requestCache,
						final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		_eventBus = eventBus;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(final CurrentUserChangedEvent event) {
						setAppUser(event.getUser());
						if (appUser != null) {
							avatar = appUser.getAvatarImage();
							_hashtags.clear();
							_hashtags.add(Constants.DEFAULT_HASHTAG);
							_hashtags.addAll(appUser.getHashtags());

							final AsyncCallback<FollowingResult> followingReq = new AsyncCallback<FollowingResult>() {
								@Override
								public void onFailure(final Throwable caught) {
									// TODO: What happens if fail?
								}

								@Override
								public void onSuccess(final FollowingResult result) {
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
		faveLists = new HashMap<String, List<FaveItem>>();
		followingResult = null;
		fullListRetrieved = false;
		_currentHashtag = Constants.DEFAULT_HASHTAG;
		_hashtags = new ArrayList<String>();
	}

	public boolean isLoggedIn() {
		return appUser != null;
	}

	public void setAppUser(final AppUser appUser) {
		this.appUser = appUser;
	}

	public void setAvatar(final String url) {
		avatar = url;
	}

	public void logout() {
		_dispatcher.execute(_restServiceFactory.auth().logout(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Void result) {
				_eventBus.fireEvent(new CurrentUserChangedEvent(null));
				Notification.show("Logged out successfully");
				_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.lists).build());
			}
		});
	}

	public List<AppUser> getFollowing() {
		return followingResult == null ? null : followingResult.getFollowing();
	}

	public void followUser(final AppUser user) {
		// Not logged in, redirect to login
		if (!isLoggedIn()) {
			_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
			return;
		}

		// Add to client
		if (!getFollowing().contains(user))
			getFollowing().add(user);

		// Add to server
		_dispatcher.execute(_restServiceFactory.user().followUser(user.getUsername()), new RestCallback<Void>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					// Roll back
					getFollowing().remove(user);
					final String errorMsg = response.getText();
					_eventBus.fireEvent(new UserUnfollowedEvent(user, errorMsg));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Already handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				_eventBus.fireEvent(new UserFollowedEvent(user));
			}
		});
	}

	public void unfollowUser(final AppUser user) {
		// Remove from client
		getFollowing().remove(user);

		// Remove from server
		_dispatcher.execute(_restServiceFactory.user().unfollowUser(user.getUsername()), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(Void result) {
				_eventBus.fireEvent(new UserUnfollowedEvent(user));
			}
		});
	}

	public void addMoreFollowing(final List<AppUser> users, final Boolean isMore) {
		followingResult.getFollowing().addAll(users);
		setFullListRetrieved(!isMore);
	}

	public void addSong(final String songId, final String song, final String artist) {
		addSong(songId, _currentHashtag, song, artist);
	}

	public void addSong(final String songId, final String hashtag, final String song, final String artist) {

		_dispatcher.execute(_restServiceFactory.user().addFaveItemForCurrentUser(hashtag, songId), new RestCallback<Void>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					// TODO: If not logged in, redirect to login page
					Notification.show(response.getText());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Already handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				Notification.show("Song added");
				final FaveItem item = new FaveItem();
				item.setSong(song);
				item.setArtist(artist);
				item.setSongID(songId);
				item.setId(songId);

				// Ensure local list in sync	
				if (getFaveLists().get(hashtag) != null) {
					getFaveLists().get(hashtag).add(item);
					_eventBus.fireEvent(new FaveItemAddedEvent(item));
				}
			}
		});
	}

	public void addFaveList(String name) {
		if (getHashtags().contains(name) || name.equals(Constants.DEFAULT_HASHTAG))
			return;

		final String listName = name;

		_dispatcher.execute(_restServiceFactory.user().addFaveListForCurrentUser(listName), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Void result) {
				getHashtags().add(listName);
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, listName)
						.with(PlaceParams.USER_PARAM, getUsername())
						.build());
			}
		});
	}

	public void deleteList(String name) {
		getHashtags().remove(name);
		faveLists.remove(name);
	}

	public List<FaveItem> getFaveList() {
		return faveLists.get(_currentHashtag);
	}

	public void setFaveList(final List<FaveItem> favelist) {
		faveLists.put(_currentHashtag, favelist);
	}

	public void addHashtag(final String hashtag) {
		_hashtags.add(hashtag);
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
		else if (appUser == obj || this == obj || appUser.equals(obj) || this.getUsername().equals(((AppUser)obj).getUsername())) {
			return true;
		}
		return false;
	}

	public Map<String, List<FaveItem>> getFaveLists() {
		return faveLists;
	}

	public void setFaveLists(final Map<String, List<FaveItem>> faveLists) {
		this.faveLists = faveLists;
	}

	public boolean isFullListRetrieved() {
		return fullListRetrieved;
	}

	public void setFullListRetrieved(final boolean fullListRetrieved) {
		this.fullListRetrieved = fullListRetrieved;
	}

	public AppUser getAppUser() {
		return appUser;
	}

	public String getCurrentHashtag() {
		return _currentHashtag;
	}

	public void setCurrentHashtag(final String currentHashtag) {
		this._currentHashtag = currentHashtag;
	}

}
