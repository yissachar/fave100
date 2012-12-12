package com.fave100.server.guice;


import com.fave100.server.filters.EncodingFilter;
import com.fave100.server.servlets.CustomRequestFactoryServlet;
import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.shared.ActionImpl;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		serve("/" + ActionImpl.DEFAULT_SERVICE_NAME)
				.with(DispatchServiceImpl.class);

		bind(CustomRequestFactoryServlet.class).in(Singleton.class);
		serve("/gwtRequest").with(CustomRequestFactoryServlet.class);
		
		bind(RemoteApiServlet.class).in(Singleton.class);
		serve("/remote_api").with(RemoteApiServlet.class);

		bind(ObjectifyFilter.class).in(Singleton.class);
		filter("/*").through(ObjectifyFilter.class);

		bind(EncodingFilter.class).in(Singleton.class);
		filter("/*").through(EncodingFilter.class);
	}
}
