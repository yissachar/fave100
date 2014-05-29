package com.fave100.client;

import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.shared.Constants;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class Fave100Bootstrapper implements Bootstrapper {

	public static final String SMALL_DISPLAY_STYLE = AppClientBundle.INSTANCE.getGlobalCss().smallDisplay();
	public static final String MEDIUM_DISPLAY_STYLE = AppClientBundle.INSTANCE.getGlobalCss().mediumDisplay();
	public static final String LARGE_DISPLAY_STYLE = AppClientBundle.INSTANCE.getGlobalCss().largeDisplay();

	private PlaceManager _placeManager;
	private EventBus _eventBus;
	private RestDispatchAsync _dispatcher;
	private RestServiceFactory _restServiceFactory;

	@Inject
	public Fave100Bootstrapper(final PlaceManager placeManager, final EventBus eventBus, final RestDispatchAsync dispatcher, final RestServiceFactory restServiceFactory) {
		_placeManager = placeManager;
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
	}

	@Override
	public void onBootstrap() {
		// On first page load or page refresh, check for an existing logged in user
		_dispatcher.execute(_restServiceFactory.user().getLoggedInUser(), new AsyncCallback<AppUser>() {

			@Override
			public void onFailure(Throwable caught) {
				_placeManager.revealCurrentPlace();
			}

			@Override
			public void onSuccess(AppUser appUser) {
				_eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
				_placeManager.revealCurrentPlace();
			}
		});

		AppClientBundle.INSTANCE.getGlobalCss().ensureInjected();

		if (Utils.isTouchDevice())
			AppClientBundle.INSTANCE.getMobileCss().ensureInjected();

		determineMobileStyle();

		// Constantly check for window size for responsive design
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				determineMobileStyle();
			}
		});
	}

	private void determineMobileStyle() {
		RootPanel.get().removeStyleName(SMALL_DISPLAY_STYLE);
		RootPanel.get().removeStyleName(MEDIUM_DISPLAY_STYLE);
		RootPanel.get().removeStyleName(LARGE_DISPLAY_STYLE);

		if (Window.getClientWidth() <= Constants.MOBILE_WIDTH_PX) {
			RootPanel.get().addStyleName(SMALL_DISPLAY_STYLE);
		}

		if (Window.getClientWidth() > Constants.MOBILE_WIDTH_PX && Window.getClientWidth() <= Constants.MEDIUM_DISPLAY_WIDTH_PX) {
			RootPanel.get().addStyleName(MEDIUM_DISPLAY_STYLE);
		}

		if (Window.getClientWidth() > Constants.MEDIUM_DISPLAY_WIDTH_PX) {
			RootPanel.get().addStyleName(LARGE_DISPLAY_STYLE);
		}
	}
}
