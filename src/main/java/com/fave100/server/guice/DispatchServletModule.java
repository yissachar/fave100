package com.fave100.server.guice;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fave100.server.api.AuthApi;
import com.fave100.server.api.FaveListsApi;
import com.fave100.server.api.SearchApi;
import com.fave100.server.api.SongApi;
import com.fave100.server.api.TrendingApi;
import com.fave100.server.api.UserApi;
import com.fave100.server.api.UsersApi;
import com.fave100.server.filters.EncodingFilter;
import com.fave100.server.servlets.HashtagBuilderServlet;
import com.fave100.server.servlets.HashtagEnqueuerServlet;
import com.fave100.server.servlets.PasswordCleanupServlet;
import com.fave100.shared.Constants;
import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class DispatchServletModule extends ServletModule {

	@Override
	public void configureServlets() {
		bind(JacksonJsonProvider.class).in(Singleton.class);

		bind(UsersApi.class);
		bind(AuthApi.class);
		bind(UserApi.class);
		bind(FaveListsApi.class);
		bind(SongApi.class);
		bind(SearchApi.class);
		bind(TrendingApi.class);
		serve(Constants.API_PATH + "/*").with(GuiceContainer.class);

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
