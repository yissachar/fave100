package com.fave100.server.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class GuiceServiceLayer extends ServiceLayerDecorator {

	private final Injector injector;

	@Inject
	GuiceServiceLayer(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T extends Locator<?, ?>> T createLocator(java.lang.Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	@Override
	public <T extends ServiceLocator> T createServiceLocator(java.lang.Class<T> clazz) {
		return injector.getInstance(clazz);
	}
}