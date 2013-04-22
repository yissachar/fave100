package com.fave100.client.pages.users;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.SongSelectedEvent;
import com.fave100.client.pagefragments.autocomplete.SongAutocompletePresenter;
import com.fave100.client.pagefragments.favelist.FavelistPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
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
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UsersPresenter> {
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();
	@ContentSlot public static final Type<RevealContentHandler<?>> FAVELIST_SLOT = new Type<RevealContentHandler<?>>();

	public static final String USER_PARAM = "u";

	private String requestedUsername;
	private final ApplicationRequestFactory requestFactory;
	private final PlaceManager placeManager;
	private final EventBus eventBus;
	private CurrentUser currentUser;
	@Inject SongAutocompletePresenter songAutocomplete;
	@Inject FavelistPresenter favelist;

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
	}

	@Override
	protected void onBind() {
		super.onBind();
		SongSelectedEvent.register(eventBus, new SongSelectedEvent.Handler() {
			@Override
			public void onSongSelected(final SongSelectedEvent event) {
				favelist.addSong(event.getSong().getId());
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(AUTOCOMPLETE_SLOT, songAutocomplete);
		setInSlot(FAVELIST_SLOT, favelist);
	}

	@Override
	protected void onHide() {
		super.onHide();
		// Clear the favelist
		favelist.clearFavelist();
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

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
						getView().setUserProfile(user);
						// Check if user is the currently logged in user
						if (currentUser.isLoggedIn() && currentUser.equals(user)) {
							getView().showOwnPage();
						}
						else {
							getView().showOtherPage();
						}

						favelist.setUser(user);
						favelist.refreshFavelist();

						getProxy().manualReveal(UsersPresenter.this);
					}
					else {
						placeManager.revealDefaultPlace();
					}
				}
			});
		}
	}

	@Override
	public void songSelected(final SongProxy song) {
		eventBus.fireEvent(new SongSelectedEvent(song));
	}
}

interface UsersUiHandlers extends UiHandlers {
	void songSelected(SongProxy song);
}
