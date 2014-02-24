package com.fave100.client;

import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.services.RestServiceFactory;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Utils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class Fave100Bootstrapper implements Bootstrapper {

	private PlaceManager _placeManager;
	private EventBus _eventBus;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _restServiceFactory;

	@Inject
	public Fave100Bootstrapper(final PlaceManager placeManager, final EventBus eventBus, final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		_placeManager = placeManager;
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
	}

	@Override
	public void onBootstrap() {
		// On first page load or page refresh, check for an existing logged in user
		_dispatcher.execute(_restServiceFactory.appuser().getLoggedInAppUser(), new AsyncCallback<AppUser>() {

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
	}
}
