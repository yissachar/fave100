package com.fave100.server.guice;

import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.googlecode.objectify.ObjectifyService;
import com.fave100.server.domain.AppUser;
import com.fave100.server.domain.FaveItem;
import com.fave100.server.domain.GoogleID;
import com.fave100.server.domain.Song;
import com.fave100.server.guice.ServerModule;
import com.fave100.server.guice.DispatchServletModule;

public class GuiceServletConfig extends GuiceServletContextListener {
	
	static{// TODO: Switch to Objectify 4
		ObjectifyService.register(FaveItem.class);
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Song.class);
		ObjectifyService.register(GoogleID.class);
	}

	@Override
	protected Injector getInjector() {
		return Guice
				.createInjector(new ServerModule(), new DispatchServletModule());
	}
}
