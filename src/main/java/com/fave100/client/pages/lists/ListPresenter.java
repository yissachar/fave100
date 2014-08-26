package com.fave100.client.pages.lists;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.entities.SongDto;
import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.events.favelist.HideSideBarEvent;
import com.fave100.client.events.favelist.ListChangedEvent;
import com.fave100.client.events.song.SongSelectedEvent;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.pages.PagePresenter;
import com.fave100.client.pages.lists.widgets.addsongsearch.AddSongSearchPresenter;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;
import com.fave100.client.pages.lists.widgets.globallistdetails.AddListAfterLoginAction;
import com.fave100.client.pages.lists.widgets.globallistdetails.GlobalListDetailsPresenter;
import com.fave100.client.pages.lists.widgets.listmanager.ListManagerPresenter;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.shared.Constants;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
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
		void setPageDetails(AppUser requestedUser, CurrentUser currentUser);

		String getFixedSearchStyle();

		void showUserNotFound();

		void setFollowCTA(boolean show, boolean starred);

		void toggleSideBar();

		void hideSideBar();

		void showSideBar();

		void resize();
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
	@Inject AddSongSearchPresenter search;

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

		CurrentUserChangedEvent.register(_eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						if (event.getUser() != null)
							showPage();
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
		_requestedHashtag = placeRequest.getParameter(PlaceParams.LIST_PARAM, Constants.DEFAULT_HASHTAG);
		// Possible combinations:
		// Blank user, blank list => global fave100 list
		// List only => global list for that hashtag
		// User only => user's fave100 list
		// List + user => user's list for that hashtag

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
					if (!requestedUser.getHashtags().contains(_requestedHashtag))
						_requestedHashtag = Constants.DEFAULT_HASHTAG;
					showPage();
				}
			});

			_api.call(_api.service().user().isFollowing(requestedUsername), new AsyncCallback<BooleanResult>() {

				@Override
				public void onFailure(Throwable caught) {
					isFollowing = false;
					getView().setFollowCTA(!_currentUser.isLoggedIn(), isFollowing);
				}

				@Override
				public void onSuccess(BooleanResult result) {
					isFollowing = result.getValue();
					getView().setFollowCTA(!_currentUser.isLoggedIn(), isFollowing);
					showPage();
				}
			});
		}
	}

	private void showPage() {
		getView().setPageDetails(requestedUser, _currentUser);

		_ownPage = _currentUser.isLoggedIn() && _currentUser.equals(requestedUser);
		if (_ownPage) {
			getView().setFollowCTA(false, isFollowing);
		}
		else {
			getView().setFollowCTA(true, isFollowing);
		}

		favelist.setUser(requestedUser);
		favelist.setHashtag(_requestedHashtag);
		favelist.refreshFavelist();

		listManager.setUser(requestedUser);
		listManager.setHashtag(_requestedHashtag);
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
	public void showSongSearch() {
		addToPopupSlot(search);
	}

}

interface ListUiHandlers extends UiHandlers {
	void songSelected(SongDto song);

	void followUser();

	boolean isOwnPage();

	void contributeToList();

	void showSongSearch();
}
