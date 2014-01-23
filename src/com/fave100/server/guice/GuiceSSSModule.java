package com.fave100.server.guice;

import java.util.HashSet;
import java.util.Set;

import com.fave100.server.domain.SongApi;
import com.fave100.server.domain.WhylineApi;
import com.fave100.server.domain.appuser.AppUserApi;
import com.fave100.server.domain.favelist.FaveListApi;
import com.google.api.server.spi.guice.GuiceSystemServiceServletModule;

public class GuiceSSSModule extends GuiceSystemServiceServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();

		Set<Class<?>> serviceClasses = new HashSet<Class<?>>();
		serviceClasses.add(FaveListApi.class);
		serviceClasses.add(SongApi.class);
		serviceClasses.add(AppUserApi.class);
		serviceClasses.add(WhylineApi.class);
		this.serveGuiceSystemServiceServlet("/_ah/spi/*", serviceClasses);
	}
}