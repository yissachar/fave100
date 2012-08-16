package com.fave100.client;

import com.google.gwt.core.client.EntryPoint;
import com.fave100.client.gin.ClientGinjector;
import com.fave100.client.requestfactory.AppUserRequest;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

public class Fave100 implements EntryPoint {

	private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

	@Override
	public void onModuleLoad() {
		// TODO: HTTPS
		// TODO: Gatekeepers
		// TODO: import playlist from iTunes
		// TODO: indexed vs. unindexed
		
		// This is required for Gwt-Platform proxy's generator		
		DelayedBindRegistry.bind(ginjector);
		
		checkPassword();
	
		//ginjector.getPlaceManager().revealCurrentPlace();
	}
	
	private void checkPassword() {
		ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
		requestFactory.initialize(new SimpleEventBus());
		AppUserRequest appUserRequest = requestFactory.appUserRequest();
		Request<Boolean> correctPasswordReq = appUserRequest.checkPassword(Window.prompt("Please enter the password:", ""));
		correctPasswordReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(Boolean response) {			
				if(response != true) {
					checkPassword();
				} else {					
					ginjector.getPlaceManager().revealCurrentPlace();
				}
			}
		});
	}
}
