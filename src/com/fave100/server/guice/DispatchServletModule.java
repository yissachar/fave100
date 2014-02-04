package com.fave100.server.guice;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.fave100.server.domain.appuser.AppUserApi;
import com.fave100.server.domain.favelist.FaveListApi;
import com.fave100.server.filters.EncodingFilter;
import com.fave100.server.servlets.HashtagBuilderServlet;
import com.fave100.server.servlets.HashtagEnqueuerServlet;
import com.fave100.server.servlets.PasswordCleanupServlet;
import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		bind(JacksonJsonProvider.class).in(Singleton.class);

		bind(AppUserApi.class);
		bind(FaveListApi.class);
		serve("/rest/*").with(GuiceContainer.class);

		bind(RemoteApiServlet.class).in(Singleton.class);
		serve("/remote_api").with(RemoteApiServlet.class);

		bind(PasswordCleanupServlet.class).in(Singleton.class);
		serve("/cron/pwdcleanup").with(PasswordCleanupServlet.class);

		bind(HashtagEnqueuerServlet.class).in(Singleton.class);
		serve(HashtagEnqueuerServlet.HASHTAG_ENQUEUER_URL).with(HashtagEnqueuerServlet.class);

		bind(HashtagBuilderServlet.class).in(Singleton.class);
		serve(HashtagBuilderServlet.HASHTAG_BUILDER_URL).with(HashtagBuilderServlet.class);

		bind(ObjectifyFilter.class).in(Singleton.class);
		filter("/*").through(ObjectifyFilter.class);

		bind(EncodingFilter.class).in(Singleton.class);
		filter("/*").through(EncodingFilter.class);
	}
}
