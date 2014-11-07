package com.fave100.client.pages.lists;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.Notification;
import com.fave100.client.entities.SongDto;
import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.events.favelist.HideSideBarEvent;
import com.fave100.client.events.favelist.ListChangedEvent;
import com.fave100.client.events.song.SongSelectedEvent;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.pages.PagePresenter;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;
import com.fave100.client.pages.lists.widgets.globallistdetails.AddListAfterLoginAction;
import com.fave100.client.pages.lists.widgets.globallistdetails.GlobalListDetailsPresenter;
import com.fave100.client.pages.lists.widgets.listmanager.ListManagerPresenter;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.client.widgets.search.SearchType;
import com.fave100.client.widgets.search.SuggestionSelectedAction;
import com.fave100.client.widgets.searchpopup.PopupSearchPresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.ListMode;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class ListPresenter extends PagePresenter<ListPresenter.MyView, ListPresenter.MyProxy> implements ListUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListUiHandlers> {
		void setPageDetails(AppUser requestedUser, CurrentUser currentUser, boolean isTrendingList);

		String getFixedSearchStyle();

		void showUserNotFound();

		void setFollowCTA(boolean show, boolean starred);

		void toggleSideBar();

		void hideSideBar();

		void showSideBar();

		void resize();

		void setCriticUrl(String url);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.lists)
	public interface MyProxy extends ProxyPlace<ListPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> FAVELIST_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> STARRED_LISTS_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> LIST_MANAGER_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> GLOBAL_LIST_DETAILS_SLOT = new Type<RevealContentHandler<?>>();

	private String requestedUsername;
	private String _requestedHashtag;
	private String _requestedListMode;
	private boolean isFollowing;
	private AppUser requestedUser;
	private final EventBus _eventBus;
	private PlaceManager _placeManager;
	private CurrentUser _currentUser;
	private FaveApi _api;
	private boolean _ownPage = false;
	@Inject FavelistPresenter favelist;
	@Inject UsersFollowingPresenter usersFollowing;
	@Inject ListManagerPresenter listManager;
	@Inject GlobalListDetailsPresenter globalListDetails;
	@Inject PopupSearchPresenter _search;

	@Inject
	public ListPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager, final CurrentUser currentUser,
							final FaveApi api) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_currentUser = currentUser;
		_api = api;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		_search.setSearchType(SearchType.SONGS);
		_search.setSuggestionSelectedAction(new SuggestionSelectedAction() {

			@Override
			public void execute(SearchType searchType, Object selectedItem) {
				SongDto song = (SongDto)selectedItem;

				PlaceRequest currentPlace = _placeManager.getCurrentPlaceRequest();
				String listName = currentPlace.getParameter(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG);
				_currentUser.addSong(song.getId(), listName, song.getSong(), song.getArtist());
				hideAddSongPrompt();
			}
		});

		SongSelectedEvent.register(_eventBus, new SongSelectedEvent.Handler() {
			@Override
			public void onSongSelected(final SongSelectedEvent event) {
				final SongDto song = event.getSong();
				favelist.addSong(song.getId(), song.getSong(), song.getArtist(), true);
			}
		});

		UserFollowedEvent.register(_eventBus, new UserFollowedEvent.Handler() {
			@Override
			public void onUserFollowed(final UserFollowedEvent event) {
				isFollowing = true;
				getView().setFollowCTA(!_currentUser.equals(requestedUser), isFollowing);
			}
		});

		UserUnfollowedEvent.register(_eventBus, new UserUnfollowedEvent.Handler() {
			@Override
			public void onUserUnfollowed(final UserUnfollowedEvent event) {
				isFollowing = false;
				getView().setFollowCTA(!_currentUser.equals(requestedUser), isFollowing);
			}
		});

		ListChangedEvent.register(_eventBus, new ListChangedEvent.Handler() {
			@Override
			public void onListChanged(final ListChangedEvent event) {
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.LIST_PARAM, event.getList())
						.build());
			}
		});

		HideSideBarEvent.register(_eventBus, new HideSideBarEvent.Handler() {

			@Override
			public void onHideSideBar(HideSideBarEvent event) {
				getView().toggleSideBar();
			}
		});

		addRegisteredHandler(NavigationEvent.getType(), new NavigationHandler() {

			@Override
			public void onNavigation(NavigationEvent navigationEvent) {
				getView().hideSideBar();
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(FAVELIST_SLOT, favelist);
		setInSlot(STARRED_LISTS_SLOT, usersFollowing);
		setInSlot(LIST_MANAGER_SLOT, listManager);
		setInSlot(GLOBAL_LIST_DETAILS_SLOT, globalListDetails);
		getView().resize();
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Clear the favelist
		favelist.clearFavelist();
		usersFollowing.clearLists();
		getView().setFollowCTA(false, false);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		requestedUser = null;
		isFollowing = false;
		// Use parameters to determine what to reveal on page
		requestedUsername = placeRequest.getParameter(PlaceParams.USER_PARAM, "");
		String defaultHashtag = requestedUsername.isEmpty() ? Constants.TRENDING_LIST_NAME : Constants.DEFAULT_HASHTAG;
		_requestedHashtag = placeRequest.getParameter(PlaceParams.LIST_PARAM, defaultHashtag);
		_requestedListMode = placeRequest.getParameter(PlaceParams.MODE_PARAM, ListMode.USERS);

		// Possible combinations:
		// Blank user, blank list => global fave100 list
		// List only => global list for that hashtag, all list mode
		// User only => user's fave100 list
		// List + user => user's list for that hashtag
		// List + list mode => global list for that hashtag, specified list mode

		if (requestedUsername.isEmpty()) {
			// No user, just show global list for hashtag
			showPage();
		}
		else {
			// Get user profile

			// If current user just grab the local info and show
			if (requestedUsername.equals(_currentUser.getUsername())) {
				requestedUser = _currentUser;
				_currentUser.setCurrentHashtag(_requestedHashtag);
				isFollowing = false;
				getView().setFollowCTA(false, isFollowing);
				showPage();
				return;
			}

			// Otherwise, request the info from the server
			_api.call(_api.service().users().getAppUser(requestedUsername), new AsyncCallback<AppUser>() {

				@Override
				public void onFailure(Throwable caught) {
					getView().showUserNotFound();
					getProxy().manualReveal(ListPresenter.this);
				}

				@Override
				public void onSuccess(AppUser user) {
					requestedUser = user;
					if (!requestedUser.getHashtags().contains(_requestedHashtag)) {
						_requestedHashtag = Constants.DEFAULT_HASHTAG;
					}
					showPage();

				}
			});

			_api.call(_api.service().user().isFollowing(requestedUsername), new AsyncCallback<BooleanResult>() {

				@Override
				public void onFailure(Throwable caught) {
					isFollowing = false;
					refreshFollowCTA();
				}

				@Override
				public void onSuccess(BooleanResult result) {
					isFollowing = result.getValue();
					refreshFollowCTA();
				}
			});
		}
	}

	private void showPage() {
		_ownPage = _currentUser.isLoggedIn() && _currentUser.equals(requestedUser);
		refreshFollowCTA();

		if (requestedUser != null && requestedUser.isCritic()) {
			_api.call(_api.service().users().getCriticUrl(requestedUsername, _requestedHashtag), new RestCallback<StringResult>() {

				@Override
				public void onFailure(Throwable caught) {
					getProxy().manualReveal(ListPresenter.this);
				}

				@Override
				public void onSuccess(StringResult result) {
					if (_ownPage) {
						getView().setCriticUrl(result.getValue());
						getProxy().manualReveal(ListPresenter.this);
					}
					else {
						Window.Location.replace(result.getValue());
					}
				}

				@Override
				public void setResponse(Response response) {
					if (response.getStatusCode() >= 400) {
						Notification.show("Couldn't get the critic URL", true);
					}
				}
			});
		}

		// Ensure we don't show critic's lists directly to other users
		if (requestedUser == null || !requestedUser.isCritic() || _ownPage) {

			getView().setPageDetails(requestedUser, _currentUser, Constants.TRENDING_LIST_NAME.equals(_requestedHashtag));

			favelist.setUser(requestedUser);
			favelist.setHashtag(_requestedHashtag);
			favelist.setListMode(_requestedListMode);
			favelist.refreshFavelist();

			listManager.setUser(requestedUser);
			listManager.setHashtag(_requestedHashtag);
			listManager.setListMode(_requestedListMode);
			listManager.refreshUsersLists();

			if (requestedUser != null) {
				usersFollowing.getView().show();
				usersFollowing.setUser(requestedUser);
				usersFollowing.refreshLists();
				globalListDetails.getView().hide();
			}
			else {
				usersFollowing.getView().hide();
				if (_requestedHashtag != null) {
					globalListDetails.getView().show();
				}
			}

			getProxy().manualReveal(ListPresenter.this);
		}

	}

	private void refreshFollowCTA() {
		if (_ownPage) {
			getView().setFollowCTA(false, isFollowing);
		}
		else {
			getView().setFollowCTA(true, isFollowing);
		}
	}

	@Override
	public void songSelected(final SongDto song) {
		_eventBus.fireEvent(new SongSelectedEvent(song));
	}

	@Override
	public void followUser() {
		if (!isFollowing) {
			_currentUser.followUser(requestedUser);
		}
		else {
			_currentUser.unfollowUser(requestedUser);
		}
	}

	@Override
	public boolean isOwnPage() {
		return _ownPage;
	}

	@Override
	public void contributeToList() {
		if (_currentUser.isLoggedIn()) {
			// If user already has that list, switch to it
			if (_currentUser.getHashtags().contains(_requestedHashtag) || _requestedHashtag.equals(Constants.DEFAULT_HASHTAG)) {
				_placeManager.revealPlace(new PlaceRequest.Builder()
						.nameToken(NameTokens.lists)
						.with(PlaceParams.USER_PARAM, _currentUser.getUsername())
						.with(PlaceParams.LIST_PARAM, _requestedHashtag)
						.build());
			}
			// Otherwise create the list for them
			else {
				_currentUser.addFaveList(_requestedHashtag);
			}
		}
		else {
			_currentUser.setAfterLoginAction(new AddListAfterLoginAction(this));
			_eventBus.fireEvent(new LoginDialogRequestedEvent());
		}
	}

	@Override
	public void showAddSongPrompt() {
		favelist.resetDirection();
		addToPopupSlot(_search);
	}

	@Override
	public void hideAddSongPrompt() {
		removeFromPopupSlot(_search);
	}

	@Override
	public void saveCriticUrl(String url) {
		_api.call(_api.service().users().setCriticUrl(requestedUsername, _requestedHashtag, url), new RestCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// Handled in setResponse
			}

			@Override
			public void onSuccess(Void result) {
				Notification.show("Saved");
			}

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					Notification.show("Something went wrong", true);
				}

			}
		});
	}

	@Override
	public void switchListDirection() {
		favelist.switchDirection();
	}

}

interface ListUiHandlers extends UiHandlers {
	void songSelected(SongDto song);

	void followUser();

	boolean isOwnPage();

	void contributeToList();

	void showAddSongPrompt();

	void hideAddSongPrompt();

	void saveCriticUrl(String url);

	void switchListDirection();
}
