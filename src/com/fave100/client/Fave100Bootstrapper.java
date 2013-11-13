package com.fave100.client;

import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.resources.css.AppClientBundle;
import com.fave100.shared.Utils;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

public class Fave100Bootstrapper implements Bootstrapper {

	private PlaceManager _placeManager;
	private EventBus _eventBus;

	@Inject
	public Fave100Bootstrapper(final PlaceManager placeManager, final EventBus eventBus) {
		_placeManager = placeManager;
		_eventBus = eventBus;
	}

	@Override
	public void onBootstrap() {
		// On first page load or page refresh, check for an existing logged in user
		final ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(_eventBus);
		final Request<AppUserProxy> request = requestFactory.appUserRequest().getLoggedInAppUser();
		request.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				_eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
				_placeManager.revealCurrentPlace();

			}

			@Override
			public void onFailure(final ServerFailure failure) {
				_placeManager.revealCurrentPlace();
			}
		});

		AppClientBundle.INSTANCE.getGlobalCss().ensureInjected();

		if (Utils.isTouchDevice())
			AppClientBundle.INSTANCE.getMobileCss().ensureInjected();
	}
}
