package com.fave100.client;

import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUserDto;
import com.fave100.client.generated.services.AppUserService;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.shared.Utils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestDispatchAsync;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class Fave100Bootstrapper implements Bootstrapper {

	private PlaceManager _placeManager;
	private EventBus _eventBus;
	private RestDispatchAsync _dispatcher;
	private AppUserService _appUserService;

	@Inject
	public Fave100Bootstrapper(final PlaceManager placeManager, final EventBus eventBus, final RestDispatchAsync dispatcher, final AppUserService appUserService) {
		_placeManager = placeManager;
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_appUserService = appUserService;
	}

	@Override
	public void onBootstrap() {
		// On first page load or page refresh, check for an existing logged in user
		_dispatcher.execute(_appUserService.getLoggedInAppUser(), new AsyncCallback<AppUserDto>() {

			@Override
			public void onFailure(Throwable caught) {
				_placeManager.revealCurrentPlace();
			}

			@Override
			public void onSuccess(AppUserDto appUser) {
				_eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
				_placeManager.revealCurrentPlace();
			}
		});

		AppClientBundle.INSTANCE.getGlobalCss().ensureInjected();

		if (Utils.isTouchDevice())
			AppClientBundle.INSTANCE.getMobileCss().ensureInjected();
	}
}
