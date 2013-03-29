package com.fave100.client;

import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.gin.ClientGinjector;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class Fave100 implements EntryPoint {

	private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

	@Override
	public void onModuleLoad() {
		// TODO: HTTPS
		// TODO: import playlist from iTunes
		// TODO: Backend cron job that cleans up expired password tokens

		// This is required for Gwt-Platform proxy's generator
		DelayedBindRegistry.bind(ginjector);

		// On first page load or page refresh, check for an existing logged in user
		final ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(ginjector.getEventBus());
		final Request<AppUserProxy> request = requestFactory.appUserRequest().getLoggedInAppUser();
		request.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				ginjector.getEventBus().fireEvent(new CurrentUserChangedEvent(appUser));
				ginjector.getPlaceManager().revealCurrentPlace();

			}

			@Override
			public void onFailure(final ServerFailure failure) {
				ginjector.getPlaceManager().revealCurrentPlace();
			}
		});

	}
}
