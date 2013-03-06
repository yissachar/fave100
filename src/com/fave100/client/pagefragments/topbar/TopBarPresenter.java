package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.events.SongSelectedEvent;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.SongProxy;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
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
 *
 * @author yissachar.radcliffe
 *
 */
public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView>
		implements TopBarUiHandlers {

	public interface MyView extends View, HasUiHandlers<TopBarUiHandlers> {
		void setLoggedIn(String username);

		void setLoggedOut();
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>>	LOGIN_SLOT	= new Type<RevealContentHandler<?>>();

	@Inject
	private LoginWidgetPresenter						loginBox;
	private EventBus									eventBus;
	private PlaceManager								placeManager;
	private CurrentUser									currentUser;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
			final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setTopBar();
					}
				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		setInSlot(LOGIN_SLOT, loginBox);
		setTopBar();
	}

	private void setTopBar() {
		if (currentUser != null && currentUser.isLoggedIn()) {
			getView().setLoggedIn(currentUser.getUsername());
		} else {
			getView().setLoggedOut();
		}
	}

	@Override
	public void songSelected(final SongProxy song) {
		// If we are on the users page, fire off an event
		if (placeManager.getCurrentPlaceRequest().getNameToken()
				.equals(NameTokens.users)) {
			eventBus.fireEvent(new SongSelectedEvent(song));
		} else {
			// Otherwise, redirect to the song page
			placeManager.revealPlace(new PlaceRequest(NameTokens.song).with(
					"song", song.getSong()).with("artist",
					song.getArtist()));
		}
	}
}

interface TopBarUiHandlers extends UiHandlers {
	void songSelected(SongProxy song);
}
