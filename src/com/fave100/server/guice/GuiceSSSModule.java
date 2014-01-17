package com.fave100.server.guice;

import java.util.HashSet;
import java.util.Set;

import com.fave100.server.domain.favelist.Fave100Api;
import com.google.api.server.spi.guice.GuiceSystemServiceServletModule;

public class GuiceSSSModule extends GuiceSystemServiceServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();

		Set<Class<?>> serviceClasses = new HashSet<Class<?>>();
		serviceClasses.add(Fave100Api.class);
		this.serveGuiceSystemServiceServlet("/_ah/spi/*", serviceClasses);
	}
}