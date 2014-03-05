package com.fave100.server;

import com.fave100.server.filters.EncodingFilter;
import com.fave100.server.servlets.HashtagBuilderServlet;
import com.fave100.server.servlets.HashtagEnqueuerServlet;
import com.fave100.server.servlets.PasswordCleanupServlet;
import com.fave100.shared.Constants;
import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.googlecode.objectify.ObjectifyFilter;
import com.leacox.dagger.jersey.DaggerContainer;
import com.leacox.dagger.servlet.DaggerServletContextListener;

public class ServletConfig extends DaggerServletContextListener {

	@Override
	protected Class<?>[] getBaseModules() {
		return new Class<?>[] {ServerModule.class};
	}

	@Override
	protected Object[] getRequestScopedModules() {
		return new Class<?>[] {};
	}

	@Override
	protected void configureServlets() {
		serve(Constants.API_PATH + "/*").with(DaggerContainer.class);

		serve("/remote_api").with(RemoteApiServlet.class);

		serve("/cron/pwdcleanup").with(PasswordCleanupServlet.class);

		serve(HashtagEnqueuerServlet.HASHTAG_ENQUEUER_URL).with(HashtagEnqueuerServlet.class);

		serve(HashtagBuilderServlet.HASHTAG_BUILDER_URL).with(HashtagBuilderServlet.class);

		filter("/*").through(ObjectifyFilter.class);

		filter("/*").through(EncodingFilter.class);
	}
}
