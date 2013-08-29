package com.fave100.client.pages.lists;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.favelist.RankInputUnfocusEvent;
import com.fave100.client.events.song.SongSelectedEvent;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.events.user.UserFollowedEvent;
import com.fave100.client.events.user.UserUnfollowedEvent;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.lists.widgets.autocomplete.SongAutocompletePresenter;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;
import com.fave100.client.pages.lists.widgets.globallistdetails.GlobalListDetailsPresenter;
import com.fave100.client.pages.lists.widgets.listmanager.ListManagerPresenter;
import com.fave100.client.pages.lists.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.HashtagProxy;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class ListPresenter extends
		BasePresenter<ListPresenter.MyView, ListPresenter.MyProxy>
		implements ListUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<ListUiHandlers> {
		void setUserProfile(AppUserProxy user);

		void showOwnPage();

		void showOtherPage();

		String getFixedSearchStyle();

		void renderSharing(String username);

		void showUserNotFound();

		void setFollowCTA(boolean show, boolean starred);

		void setMobileView(boolean reset);
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

	public static final String USER_PARAM = "u";
	public static final String LIST_PARAM = "list";

	private String requestedUsername;
	private String _requestedHashtag;
	private boolean isFollowing;
	private HashtagProxy _hashtag;
	// For now just hardcode, only one possible hashtag
	private AppUserProxy requestedUser;
	private final ApplicationRequestFactory _requestFactory;
	private final PlaceManager _placeManager;
	private final EventBus _eventBus;
	private CurrentUser _currentUser;
	@Inject SongAutocompletePresenter songAutocomplete;
	@Inject FavelistPresenter favelist;
	@Inject UsersFollowingPresenter usersFollowing;
	@Inject ListManagerPresenter listManager;
	@Inject GlobalListDetailsPresenter globalListDetails;

	@Inject
	public ListPresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy, final ApplicationRequestFactory requestFactory,
							final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this._eventBus = eventBus;
		this._requestFactory = requestFactory;
		this._placeManager = placeManager;
		this._currentUser = currentUser;
		getView().setUiHandlers(this);

		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(final ScrollEvent event) {
				final Widget widget = songAutocomplete.asWidget();
				if (event.getScrollTop() >= 26 && Window.getClientWidth() > Constants.MOBILE_WIDTH_PX) {
					songAutocomplete.getView().hideHelp();
					songAutocomplete.getView().showBackToTop(true);
					widget.addStyleName(getView().getFixedSearchStyle());
				}
				else {
					widget.removeStyleName(getView().getFixedSearchStyle());
					songAutocomplete.getView().showBackToTop(false);
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				getView().setMobileView(false);
			}
		});
	}

	@Override
	protected void onBind() {
		super.onBind();

		SongSelectedEvent.register(_eventBus, new SongSelectedEvent.Handler() {
			@Override
			public void onSongSelected(final SongSelectedEvent event) {
				final SongProxy song = event.getSong();
				favelist.addSong(song.getId(), song.getSong(), song.getArtist());
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
						requestedUser = event.getUser();
						showPage();
					}
				});

		RankInputUnfocusEvent.register(_eventBus, new RankInputUnfocusEvent.Handler() {
			@Override
			public void onRankInputUnfocus(final RankInputUnfocusEvent event) {
				songAutocomplete.setFocus();
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(AUTOCOMPLETE_SLOT, songAutocomplete);
		setInSlot(FAVELIST_SLOT, favelist);
		setInSlot(STARRED_LISTS_SLOT, usersFollowing);
		setInSlot(LIST_MANAGER_SLOT, listManager);
		setInSlot(GLOBAL_LIST_DETAILS_SLOT, globalListDetails);
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Clear the favelist
		favelist.clearFavelist();
		usersFollowing.refreshLists();
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
		requestedUsername = placeRequest.getParameter(USER_PARAM, "");
		_requestedHashtag = placeRequest.getParameter(LIST_PARAM, Constants.DEFAULT_HASHTAG);
		// Possible combinations:
		// Blank user, blank list => global fave100 list
		// List only => global list for that hashtag
		// User only => user's fave100 list
		// List + user => user's list for that hashtag

		if (requestedUsername.isEmpty()) {
			// No user, just show global list for hashtag
			final Request<HashtagProxy> hashtagReq = _requestFactory.faveListRequest().getHashtag(_requestedHashtag);
			hashtagReq.fire(new Receiver<HashtagProxy>() {
				@Override
				public void onSuccess(final HashtagProxy hashtag) {
					_hashtag = hashtag;
					showPage();
				}
			});

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
			final AppUserRequest userReq = _requestFactory.appUserRequest();
			userReq.findAppUser(requestedUsername).to(
					new Receiver<AppUserProxy>() {
						@Override
						public void onSuccess(final AppUserProxy user) {
							if (user != null) {
								requestedUser = user;
								if (!requestedUser.getHashtags().contains(_requestedHashtag))
									_requestedHashtag = Constants.DEFAULT_HASHTAG;
								showPage();
							}
							else {
								getView().showUserNotFound();
								getProxy().manualReveal(ListPresenter.this);
							}
						}
					});
			userReq.isFollowing(requestedUsername).to(
					new Receiver<Boolean>() {
						@Override
						public void onSuccess(final Boolean following) {
							isFollowing = following;
							getView().setFollowCTA(!_currentUser.isLoggedIn(), isFollowing);
						}

						@Override
						public void onFailure(final ServerFailure failure) {
							isFollowing = false;
							if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
								_eventBus.fireEvent(new CurrentUserChangedEvent(null));
							}
							getView().setFollowCTA(!_currentUser.isLoggedIn(), isFollowing);
						}
					});
			userReq.fire();
		}
	}

	private void showPage() {
		getView().setUserProfile(requestedUser);
		final boolean ownPage = _currentUser.isLoggedIn() && _currentUser.equals(requestedUser);
		if (ownPage) {
			getView().showOwnPage();
			getView().setFollowCTA(false, isFollowing);
		}
		else {
			getView().showOtherPage();
			getView().setFollowCTA(_currentUser.isLoggedIn(), isFollowing);
		}

		favelist.setUser(requestedUser);
		favelist.setHashtag(_requestedHashtag);
		favelist.refreshFavelist(ownPage);

		if (requestedUser != null) {
			usersFollowing.getView().show();
			usersFollowing.setUser(requestedUser);
			usersFollowing.refreshLists();
			listManager.getView().show();
			listManager.setUser(requestedUser);
			listManager.setHashtag(_requestedHashtag);
			listManager.refreshUsersLists();
			globalListDetails.getView().hide();
		}
		else {
			usersFollowing.getView().hide();
			listManager.getView().hide();
			globalListDetails.getView().show();
			globalListDetails.setHashtag(_hashtag);
		}

		getView().setMobileView(true);

		getProxy().manualReveal(ListPresenter.this);

		// Now that page is visible, render FB like button				
		getView().renderSharing(requestedUsername);
	}

	@Override
	public void songSelected(final SongProxy song) {
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
}

interface ListUiHandlers extends UiHandlers {
	void songSelected(SongProxy song);

	void followUser();
}