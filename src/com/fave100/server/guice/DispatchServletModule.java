package com.fave100.server.guice;


import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.gwtplatform.dispatch.shared.ActionImpl;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		serve("/" + ActionImpl.DEFAULT_SERVICE_NAME)
				.with(DispatchServiceImpl.class);
		
		bind(RequestFactoryServlet.class).in(Singleton.class);
		serve("/gwtRequest").with(RequestFactoryServlet.class);
		}
}
