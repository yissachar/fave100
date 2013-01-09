package com.fave100.client.pagefragments.topbar;

import com.fave100.client.events.SongSelectedEvent;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Top navigation bar that will be included on every page.
 * @author yissachar.radcliffe
 *
 */
public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView>
	implements TopBarUiHandlers{

	public interface MyView extends View, HasUiHandlers<TopBarUiHandlers> {
		void setLoggedIn(String username);
		void setLoggedOut();
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private LoginWidgetPresenter loginBox;
	@Inject private ApplicationRequestFactory requestFactory;
	private EventBus eventBus;
	private PlaceManager placeManager;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
			final PlaceManager placeManager) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		 setInSlot(LOGIN_SLOT, loginBox);
		// TODO: Use manual reveal to avoid delay from AppUseRequest = but how can we use manual reveal on a presenterwidget?

		// Whenever the page is refreshed, check to see if the user is logged in or not
		// and change the top bar links and elements appropriately.

		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<AppUserProxy> getLoggedInAppUserReq = appUserRequest.getLoggedInAppUser();
		getLoggedInAppUserReq.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				if(appUser != null) {
					getView().setLoggedIn(appUser.getUsername());
				} else {
					getView().setLoggedOut();
				}
			}
		});
	}

	@Override
	public void songSelected(final SongProxy song) {
		if(placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.users)) {
			eventBus.fireEvent(new SongSelectedEvent(song));
		} else {
			placeManager.revealPlace(new PlaceRequest(NameTokens.song)
				.with("song", song.getTrackName()).with("artist", song.getArtistName()));
		}
	}
}

interface TopBarUiHandlers extends UiHandlers {
	void songSelected(SongProxy song);
}
