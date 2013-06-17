package com.fave100.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.ApplicationController;

public class Fave100 implements EntryPoint {

	private static final ApplicationController controller = GWT.create(ApplicationController.class);

	@Override
	public void onModuleLoad() {
		controller.init();
		// TODO: HTTPS
		// TODO: import playlist from iTunes
	}
}
