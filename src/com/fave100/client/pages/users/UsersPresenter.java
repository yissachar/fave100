package com.fave100.client.pages.users;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.SongSelectedEvent;
import com.fave100.client.events.UserFollowedEvent;
import com.fave100.client.events.UserUnfollowedEvent;
import com.fave100.client.pagefragments.autocomplete.SongAutocompletePresenter;
import com.fave100.client.pagefragments.favelist.FavelistPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.users.widgets.usersfollowing.UsersFollowingPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
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
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class UsersPresenter extends
		BasePresenter<UsersPresenter.MyView, UsersPresenter.MyProxy>
		implements UsersUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<UsersUiHandlers> {
		void setUserProfile(AppUserProxy user);

		void showOwnPage();

		void showOtherPage();

		String getFixedSearchStyle();

		void renderSharing();

		void showUserNotFound();

		void setFollowCTA(boolean show, boolean starred);

		void setMobileView();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UsersPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> FAVELIST_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> STARRED_LISTS_SLOT = new Type<RevealContentHandler<?>>();

	public static final String USER_PARAM = "u";

	private String requestedUsername;
	// For now just hardcode, only one possible hashtag
	private AppUserProxy requestedUser;
	private final ApplicationRequestFactory requestFactory;
	private final PlaceManager placeManager;
	private final EventBus eventBus;
	private CurrentUser currentUser;
	@Inject SongAutocompletePresenter songAutocomplete;
	@Inject FavelistPresenter favelist;
	@Inject UsersFollowingPresenter usersFollowing;

	@Inject
	public UsersPresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy, final ApplicationRequestFactory requestFactory,
							final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);

		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(final ScrollEvent event) {
				final Widget widget = songAutocomplete.asWidget();
				if (event.getScrollTop() >= 26 && Window.getClientWidth() > Constants.MOBILE_WIDTH_PX) {
					songAutocomplete.getView().hideHelp();
					widget.addStyleName(getView().getFixedSearchStyle());
				}
				else {
					widget.removeStyleName(getView().getFixedSearchStyle());
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				getView().setMobileView();
			}
		});
	}

	@Override
	protected void onBind() {
		super.onBind();

		SongSelectedEvent.register(eventBus, new SongSelectedEvent.Handler() {
			@Override
			public void onSongSelected(final SongSelectedEvent event) {
				final SongProxy song = event.getSong();
				favelist.addSong(song.getId(), song.getSong(), song.getArtist());
			}
		});

		UserFollowedEvent.register(eventBus, new UserFollowedEvent.Handler() {
			@Override
			public void onUserFollowed(final UserFollowedEvent event) {
				getView().setFollowCTA(!currentUser.equals(requestedUser), true);
			}
		});

		UserUnfollowedEvent.register(eventBus, new UserUnfollowedEvent.Handler() {
			@Override
			public void onUserUnfollowed(final UserUnfollowedEvent event) {
				getView().setFollowCTA(!currentUser.equals(requestedUser), false);
			}
		});

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						showPage();
					}
				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(AUTOCOMPLETE_SLOT, songAutocomplete);
		setInSlot(FAVELIST_SLOT, favelist);
		setInSlot(STARRED_LISTS_SLOT, usersFollowing);
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Clear the favelist
		favelist.clearFavelist();
		usersFollowing.refreshLists();
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		requestedUser = null;
		// Use parameters to determine what to reveal on page
		requestedUsername = placeRequest.getParameter("u", "");
		if (requestedUsername.isEmpty()) {
			// Malformed request, send the user away
			placeManager.revealDefaultPlace();
		}
		else {
			// Update user profile
			final Request<AppUserProxy> userReq = requestFactory.appUserRequest().findAppUser(requestedUsername);
			userReq.fire(new Receiver<AppUserProxy>() {
				@Override
				public void onSuccess(final AppUserProxy user) {
					if (user != null) {
						requestedUser = user;
						showPage();
					}
					else {
						getView().showUserNotFound();
						getProxy().manualReveal(UsersPresenter.this);
					}
				}
			});
		}
	}

	private void showPage() {
		final boolean starred = currentUser.isFollowingUser(requestedUser);

		getView().setUserProfile(requestedUser);
		// Check if user is the currently logged in user
		if (currentUser.isLoggedIn() && currentUser.equals(requestedUser)) {
			getView().showOwnPage();
			getView().setFollowCTA(false, starred);
		}
		else {
			getView().showOtherPage();
			getView().setFollowCTA(currentUser.isLoggedIn(), starred);
		}

		favelist.setUser(requestedUser);
		favelist.refreshFavelist();
		usersFollowing.setUser(requestedUser);
		usersFollowing.refreshLists();
		getView().setMobileView();

		getProxy().manualReveal(UsersPresenter.this);

		// Now that page is visible, render FB like button						
		getView().renderSharing();
	}

	@Override
	public void songSelected(final SongProxy song) {
		eventBus.fireEvent(new SongSelectedEvent(song));
	}

	@Override
	public void followUser() {
		if (!currentUser.isFollowingUser(requestedUser)) {
			currentUser.followUser(requestedUser);
		}
		else {
			currentUser.unfollowUser(requestedUser);
		}
	}
}

interface UsersUiHandlers extends UiHandlers {
	void songSelected(SongProxy song);

	void followUser();
}
