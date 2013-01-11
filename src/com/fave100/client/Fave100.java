package com.fave100.client;

import com.fave100.client.gin.ClientGinjector;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class Fave100 implements EntryPoint {

	private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

	@Override
	public void onModuleLoad() {
		// TODO: HTTPS
		// TODO: Gatekeepers
		// TODO: import playlist from iTunes
		// TODO: indexed vs. unindexed
		// TODO: Does email have to be separate Objectify object (to allow secure changing of email or multiple emails for account)
		// TODO: Backend cron job that cleans up expired password tokens

		// This is required for Gwt-Platform proxy's generator
		DelayedBindRegistry.bind(ginjector);

		ginjector.getPlaceManager().revealCurrentPlace();
	}
}
